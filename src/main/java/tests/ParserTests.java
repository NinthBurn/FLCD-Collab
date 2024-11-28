package tests;

import adt.Pair;
import parser.AnalysisItem;
import parser.Grammar;
import parser.LR0Parser;
import parser.State;

import java.util.*;

public class ParserTests {
    public static void main(String[] args) throws Exception {
        ArrayList<String> filenames = new ArrayList<String>(List.of("g1.txt", "gt.txt"));
        Map<String, State> expectedClosureResults = new HashMap<String, State>();
        Map<Pair<String, String>, State> expectedGotoResults = new HashMap<Pair<String, String>, State>();
        SeedClosureResults(expectedClosureResults);

        boolean closurePassed = ExecuteForClosure(filenames, expectedClosureResults);
        System.out.println();
        if (closurePassed) {
            SeedGotoResults(expectedGotoResults);
            boolean gotoPassed = ExecuteForGoto(filenames, expectedClosureResults, expectedGotoResults);
        }
    }

    static boolean ExecuteForClosure(ArrayList<String> filenames, Map<String, State> expectedClosureResults) throws Exception {
        boolean passed = true;
        for (String filename : filenames) {
            System.out.println("LOADED FILE: " + filename);
            Grammar grammar = new Grammar(filename).getEnrichedGrammar();
            LR0Parser parser = new LR0Parser(grammar);
            State generatedClosure = ClosureDEBUG(null, grammar);
            if (expectedClosureResults.get(filename).equals(generatedClosure)) {
                System.out.println("PASSED CLOSURE FOR: " + filename);
            }
            else {
                System.out.println("FAILED CLOSURE FOR: " + filename);
                passed = false;
            }
            System.out.println("\tEXPECTED:  " + expectedClosureResults.get(filename));
            System.out.println("\tGENERATED: " + generatedClosure);
        }
        return passed;
    }

    static boolean ExecuteForGoto(ArrayList<String> filenames, Map<String, State> stateInputs, Map<Pair<String, String>, State> expectedGotoResults) throws Exception {
        boolean passed = true;
        for (String filename : filenames) {
            System.out.println("LOADED FILE: " + filename);
            Grammar grammar = new Grammar(filename).getEnrichedGrammar();
            LR0Parser parser = new LR0Parser(grammar);
            for (Pair<String, String> key : expectedGotoResults.keySet().stream().filter(key -> key.first.equals(filename)).toList()) {
                State generatedGoto = GotoDEBUG(stateInputs.get(filename), key.second, grammar);
                if (generatedGoto.equals(expectedGotoResults.get(key))) {
                    System.out.println("PASSED GOTO WITH SYMBOL " + key.second + ": " + filename);
                }
                else {
                    System.out.println("FAILED GOTO WITH SYMBOL " + key.second + ": " + filename);
                    passed = false;
                }
                System.out.println("\tEXPECTED:  " + expectedGotoResults.get(key));
                System.out.println("\tGENERATED: " + generatedGoto);
            }
        }
        return passed;
    }

    static void SeedClosureResults(Map<String, State> results) {
        results.put("g1.txt", new State(
                new ArrayList<AnalysisItem>(
                        List.of(
                                new AnalysisItem("enriched_grammar", "Start", 0),
                                new AnalysisItem("Start", "a A B_second b", 0)
                        )
                )
        ));
        results.put("gt.txt", new State(
            new ArrayList<AnalysisItem>(
                    List.of(
                            new AnalysisItem("enriched_grammar", "S", 0),
                            new AnalysisItem("S", "a A", 0)
                    )
            )
        ));
    }

    static void SeedGotoResults(Map<Pair<String, String>, State> results) {
        results.put(new Pair<>("gt.txt", "S"), new State(
                new ArrayList<AnalysisItem>(
                        List.of(
                                new AnalysisItem("enriched_grammar", "S", 1)
                        )
                )
        ));
        results.put(new Pair<>("gt.txt", "a"), new State(
                new ArrayList<AnalysisItem>(
                        List.of(
                                new AnalysisItem("S", "a A", 1)
                        )
                )
        ));
        results.put(new Pair<>("gt.txt", "A"), new State(
                new ArrayList<AnalysisItem>()
        ));

        results.put(new Pair<>("g1.txt", "Start"), new State(
                new ArrayList<AnalysisItem>(
                        List.of(
                                new AnalysisItem("enriched_grammar", "Start", 1)
                        )
                )
        ));
        results.put(new Pair<>("g1.txt", "a"), new State(
                new ArrayList<AnalysisItem>(
                        List.of(
                                new AnalysisItem("Start", "a A B_second b", 1),
                                new AnalysisItem("A", "a A c", 0),
                                new AnalysisItem("A", "word", 0)
                        )
                )
        ));
        results.put(new Pair<>("g1.txt", "B_second"), new State(
                new ArrayList<AnalysisItem>()
        ));

    }

    static State ClosureDEBUG(State initialState, Grammar grammar) {
        if (initialState == null) {
            initialState = new State(
                    List.of(new AnalysisItem(
                                    grammar.getStartingSymbol(),
                                    grammar.getProductionsOfNonterminal(grammar.getStartingSymbol()).get(0)
                            )
                    )
            );
        }
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
        } while(closureChanged);

        return new State(currentClosure);
    }

    static State GotoDEBUG(State initialState, String symbol, Grammar grammar) {
        List<AnalysisItem> items = initialState.getItems();
        List<AnalysisItem> itemsContainingSymbol = new ArrayList<>();

        for(AnalysisItem item : items) {
            String symbolAtCursor = item.getSymbolAtCursor();

            if (symbolAtCursor.equals(symbol))
                itemsContainingSymbol.add(item.moveCursorRight());
        }

        return ClosureDEBUG(new State(itemsContainingSymbol), grammar);
    }
}
