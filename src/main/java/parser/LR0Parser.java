package parser;

import java.util.ArrayList;
import java.util.List;

public class LR0Parser {
    private Grammar grammar;

    public LR0Parser(Grammar initialGrammar) {
        grammar = initialGrammar.getEnrichedGrammar();
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

            if (symbolAtCursor.equals(symbol))
                itemsContainingSymbol.add(item.moveCursorRight());
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

        List<State> states = canonicalCollection.getStates();
        for(int index = 0; index < states.size(); ++index){
            for(AnalysisItem item : states.get(index).getItems()) {
                String symbol = item.getSymbolAtCursor();
                State newState = goTo(states.get(index), symbol);

                if (!newState.getItems().isEmpty()) {
                    int indexState = states.indexOf(newState);

                    if (indexState == -1) {
                        canonicalCollection.addState(newState);
                    }
                }
            }
        }

        return canonicalCollection;

    }
}
