package parser;

import adt.Pair;
import parser.table.ParserTableAction;
import parser.table.ParserTableActionType;
import parser.table.ParserTableRow;
import utils.TableBuilder;

import java.util.*;

public class LR0Parser {
    private final Grammar grammar;
    private final List<ParserTableRow> parserTable;

    private final List<Production> productions;


    public LR0Parser(Grammar initialGrammar) {
        grammar = initialGrammar.getEnrichedGrammar();
        parserTable = new ArrayList<>();
        productions = new ArrayList<>();

        for(String symbol : grammar.getNonTerminals()) {
            for(String production : grammar.getProductionsOfNonterminal(symbol)) {
                productions.add(new Production(symbol, production, productions.size()));
            }
        }
    }

    State closure(State initialState) {
        List<String> nonterminals = grammar.getNonTerminals();

        boolean closureChanged;
        List<AnalysisItem> currentClosure = new ArrayList<>(List.copyOf(initialState.getItems()));
        List<AnalysisItem> newClosure;

        do{
            newClosure = new ArrayList<>(List.copyOf(currentClosure));
            closureChanged = false;

            for (AnalysisItem item : currentClosure) {
                String symbolAtCursor = item.getSymbolAtCursor();

                if (nonterminals.contains(symbolAtCursor)) {
                    for(String rightHandSide : grammar.getProductionsOfNonterminal(symbolAtCursor)) {
                        AnalysisItem currentItem = new AnalysisItem(symbolAtCursor, rightHandSide, 0);

                        if(!currentClosure.contains(currentItem)) {
                            newClosure.add(currentItem);
                            closureChanged = true;
                        }
                    }
                }
            }

            currentClosure = newClosure;
        }while(closureChanged);

        return new State(currentClosure);
    }

    State goTo(State initialState, String symbol) {
        List<AnalysisItem> items = initialState.getItems();
        List<AnalysisItem> itemsContainingSymbol = new ArrayList<>();

        for(AnalysisItem item : items) {
            String symbolAtCursor = item.getSymbolAtCursor();

            if (symbolAtCursor.equals(symbol)) {
                AnalysisItem newItem = item.moveCursorRight();

                if(!itemsContainingSymbol.contains(newItem))
                    itemsContainingSymbol.add(newItem);

            }
        }

        return closure(new State(itemsContainingSymbol));
    }

    public CanonicalCollection getCanonicalCollection(){
        CanonicalCollection canonicalCollection = new CanonicalCollection();

        canonicalCollection.addState(
                closure(
                        new State(
                            List.of(new AnalysisItem(
                                grammar.getStartingSymbol(),
                                grammar.getProductionsOfNonterminal(grammar.getStartingSymbol()).get(0)
                                )
                            )
                        )
                )
        );

        canonicalCollection.setAcceptState(new AnalysisItem(
                grammar.getStartingSymbol(),
                grammar.getProductionsOfNonterminal(grammar.getStartingSymbol()).get(0),
                1
        ));

        List<State> states = canonicalCollection.getStates();
        for(int index = 0; index < states.size(); ++index){
            for(AnalysisItem item : states.get(index).getItems()) {
                String symbol = item.getSymbolAtCursor();
                State newState = goTo(states.get(index), symbol);

                if (!newState.getItems().isEmpty()) {
                    int indexState = states.indexOf(newState);

                    if (indexState == -1) {
                        canonicalCollection.addState(newState);
                        indexState = states.size() - 1;
                    }

                    canonicalCollection.connectStateToAnother(index, symbol, indexState);
                }
            }
        }

        return canonicalCollection;
    }

    public void buildParserTable(CanonicalCollection canonicalCollection) {
        List<State> states = canonicalCollection.getStates();

        for(int index = 0; index < states.size(); ++index) {
            ParserTableRow currentRow = new ParserTableRow(index);
            State state = states.get(index);

            for(AnalysisItem analysisItem : state.getItems())
                determineParserTableRowAction(currentRow, analysisItem, canonicalCollection);

            determineGoToColumnValues(currentRow, index, canonicalCollection);

            parserTable.add(currentRow);
        }
    }

    private void determineParserTableRowAction(ParserTableRow currentRow, AnalysisItem analysisItem, CanonicalCollection canonicalCollection) {
        try{
            if(analysisItem.equals(canonicalCollection.getAcceptState()))
                currentRow.setAction(new ParserTableAction(ParserTableActionType.ACCEPT));

            else if(analysisItem.cursorAtEnd()) {
                Integer productionNumber = -1;

                for(var production : productions)
                    if(production.compareAnalysisItem(analysisItem))
                        productionNumber = production.getProductionIndex();

                if(productionNumber == -1)
                    throw new RuntimeException("Invalid production index");

                currentRow.setAction(new ParserTableAction(ParserTableActionType.REDUCE, productionNumber));
            }

            else currentRow.setAction(new ParserTableAction(ParserTableActionType.SHIFT));

        }catch(Exception ex) {
            System.out.println(ex.getMessage() + " for row " + currentRow.getStateIndex());
        }
    }

    private void determineGoToColumnValues(ParserTableRow currentRow, Integer stateIndex, CanonicalCollection canonicalCollection) {
        try{
            var connectedStates = canonicalCollection.getConnectedStates();

            for(String symbol : grammar.getTerminals()) {
                Pair<Integer, String> stateSymbol = new Pair<>(stateIndex, symbol);

                if(connectedStates.containsKey(stateSymbol)) {
                    currentRow.setGoToColumn(symbol, connectedStates.get(stateSymbol));
                }
            }

            for(String symbol : grammar.getNonTerminals()) {
                Pair<Integer, String> stateSymbol = new Pair<>(stateIndex, symbol);

                if(connectedStates.containsKey(stateSymbol)) {
                    currentRow.setGoToColumn(symbol, connectedStates.get(stateSymbol));
                }
            }

        }catch(Exception ex) {
            System.out.println(ex.getMessage() + " for row " + currentRow.getStateIndex());
        }
    }
    public boolean parseMyBitchUp(String sequence) {
        Stack<String> inputStack = new Stack<>();
        Stack<String> workStack = new Stack<>();
        Stack<Integer> outputBand = new Stack<>();

        workStack.add("$");
        workStack.add("s0");

        inputStack.add("$");
        for(int index = sequence.length() - 1; index > -1; --index)
            inputStack.add(String.valueOf(sequence.charAt(index)));

        boolean inputAccepted = false;
        int parseStep = 0;

        while(!workStack.isEmpty()) {
            System.out.println("Parsing step #" + parseStep++);
            System.out.println("Work stack: " + workStack);
            System.out.println("Input stack: " + inputStack);
            System.out.println("Output band: " + outputBand + "\n");
            String workStackTop = workStack.peek();

            if(workStackTop.matches("s[0-9]+")) {
                int stateIndex = Integer.parseInt(workStackTop.substring(1));

                ParserTableRow row = parserTable.get(stateIndex);

                if(row.getAction().actionType.equals(ParserTableActionType.ACCEPT)) {
                    if(inputStack.size() != 1 || !inputStack.peek().equals("$")) {
                        throw new RuntimeException("Parser accept error: input is accepted before it is completely parsed/shifted");
                    }

                    inputAccepted = true;
                    break;
                }

                if(row.getAction().actionType.equals(ParserTableActionType.SHIFT)) {
                    String inputStackTop = inputStack.pop();

                    workStack.push(inputStackTop);

                    if(row.getGotoState(inputStackTop) != null)
                        workStack.push("s" + row.getGotoState(inputStackTop));
                    else throw new RuntimeException("Parser shift error: invalid goto for symbol " + inputStackTop + " from state " + row.getStateIndex());
                }

                if(row.getAction().actionType.equals(ParserTableActionType.REDUCE)) {
                    Integer productionIndex = row.getAction().productionIndex;
                    String RHS = productions.get(productionIndex).getRightHandSide();
                    String LHS = productions.get(productionIndex).getLeftHandSide();

                    outputBand.push(productionIndex);

                    String[] symbols = RHS.split(Grammar.symbolSeparator);
                    Stack<String> RHS_Stack = new Stack<>();

                    for (String symbol : symbols) {
                        RHS_Stack.push(symbol);
                    }

                    while(!RHS_Stack.isEmpty()) {
                        // pop current state
                        workStackTop = workStack.pop();
                        // pop current symbol
                        workStackTop = workStack.pop();

                        String productionSymbol = RHS_Stack.pop();

                        if(!productionSymbol.equals(workStackTop))
                            throw new RuntimeException("Parser reduce error: top of the work stack \"" + workStackTop + "\" does not match production symbol \"" + productionSymbol + "\"");
                    }

                    workStackTop = workStack.peek();
                    workStack.push(LHS);

                    ParserTableRow requiredRow = parserTable.get(Integer.parseInt(workStackTop.substring(1)));
                    workStack.push("s" + requiredRow.getGotoState(LHS));

                }

            } else throw new RuntimeException("Invalid top of working stack: " + workStackTop);
        }

        return inputAccepted;
    }

    public String parserTableToString() {
        List<String> headers = new ArrayList<>(List.of("ACTION"));

        headers.addAll(grammar.getTerminals());

        for(String symbol : grammar.getNonTerminals()) {
            if(symbol.equals(grammar.getStartingSymbol()))
                continue;

            headers.add(symbol);
        }

        List<String> rowNames = new ArrayList<>();
        List<List<String>> cellValues = new ArrayList<>();

        int index = 0;
        for(index = 0; index < parserTable.size(); ++index)
            cellValues.add(new ArrayList<>());

        index = 0;
        for(ParserTableRow row : parserTable) {
            rowNames.add("s" + index);

            var action = row.getAction();
            if(action.actionType.equals(ParserTableActionType.REDUCE))
                cellValues.get(index).add(action.actionType + " " + action.productionIndex.toString());
            else cellValues.get(index).add(action.actionType.toString());

            for(String symbol : grammar.getTerminals()) {
                Integer destinationState = row.getGotoState(symbol);

                if(destinationState != null)
                    cellValues.get(index).add("s" + destinationState);
                else cellValues.get(index).add("");
            }

            for(String symbol : grammar.getNonTerminals()) {
                if(symbol.equals(grammar.getStartingSymbol()))
                    continue;

                Integer destinationState = row.getGotoState(symbol);

                if(destinationState != null)
                    cellValues.get(index).add("s" + destinationState);
                else cellValues.get(index).add("");
            }

            index++;
        }

        String[][] finalData = cellValues.stream()
                .map(arr -> arr.toArray(String[]::new))
                .toArray(String[][]::new);

        return new TableBuilder()
                .setAlignment(TableBuilder.Alignment.CENTER)
                .addHeaders(headers.toArray(new String[0]))
                .addRowNames(rowNames.toArray(new String[0]))
                .setName("STATE")
                .setValues(finalData)
                .frame(true)
                .setBorders(TableBuilder.Borders.FRAME)
                .build();
    }
}
