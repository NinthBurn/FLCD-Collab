import adt.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Grammar {
    private final static String symbolSeparator = " ";
    private final static String productionSeparator = "|";
    private final static String productionSymbol = "->";
    private final static String epsilon = "ε";
    private List<String> terminals;
    private List<String> nonTerminals;
    private final Map<String, List<String>> productions;
    private String startingSymbol;

    public Grammar(String filename) throws Exception{
        this.productions = new HashMap<>();

        extractFromFile(filename);
    }

    private void extractFromFile(String filename) throws Exception{
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();

        this.nonTerminals = List.of(line.trim().split(symbolSeparator));

        line = reader.readLine();
        this.terminals = List.of(line.trim().split(symbolSeparator));

        line = reader.readLine();
        this.startingSymbol = line.trim();

        if(!this.nonTerminals.contains(startingSymbol))
            throw new Exception("Starting symbol is not a declared nonterminal");

        line = reader.readLine();
        while (line != null) {

            if(line.trim().isEmpty()) {
                line = reader.readLine();
                continue;
            }

            if(!line.contains(productionSymbol))
                throw new Exception("Invalid production form: " + line);

            String[] production = line.split(productionSymbol);

            if(production.length != 2)
                throw new Exception("Invalid production size: " + line);

            for(String symbol : production[0].split(symbolSeparator)) {
                if(!this.terminals.contains(symbol) && !this.nonTerminals.contains(symbol))
                    throw new Exception("Symbol is neither a terminal or nonterminal that is valid for the grammar: " + symbol);

            }

            String leftSide = production[0].trim();

//            String[] rightSide = Arrays.copyOfRange(production, 2, production.length);
            String[] rightSide = production[1].trim().split(symbolSeparator);

            if(!this.productions.containsKey(leftSide)) {
                List<String> rightSides = new ArrayList<>();
                extractProductionSequences(rightSide, rightSides);

                this.productions.put(leftSide, rightSides);

            } else {
                List<String> rightSides = this.productions.get(leftSide);
                extractProductionSequences(rightSide, rightSides);
            }

            line = reader.readLine();
        }

        StringBuilder incompleteProductionError = new StringBuilder("The following nonterminals do not have an associated production: ");
        boolean incomplete = false;
        for(String nonTerminal : this.nonTerminals)
            if(this.productions.get(nonTerminal) == null){
                incompleteProductionError.append(nonTerminal).append(" ");
                incomplete = true;
            }

        if(incomplete)
            throw new Exception(incompleteProductionError.toString());

        reader.close();
    }

    private void extractProductionSequences(String[] sequences, List<String> rightSides) throws Exception{
        StringBuilder currentSequence = new StringBuilder();

        for(String sequenceSymbol : sequences) {
            if(!sequenceSymbol.equals(productionSeparator)) {
                if(!this.terminals.contains(sequenceSymbol) && !this.nonTerminals.contains(sequenceSymbol) && !sequenceSymbol.equals(epsilon))
                    throw new Exception("Symbol is neither a terminal or nonterminal that is valid for the grammar: " + sequenceSymbol);

                currentSequence.append(sequenceSymbol).append(" ");

            } else {
                rightSides.add(currentSequence.toString());
                currentSequence.delete(0, currentSequence.length());
            }
        }

        rightSides.add(currentSequence.toString());
    }

    public boolean isContextFree() {
        for(var leftSide : productions.keySet())
            if(!nonTerminals.contains(leftSide))
                return false;

        return true;
    }

    public List<String> getTerminals() {
        return terminals;
    }

    public List<String> getNonTerminals() {
        return nonTerminals;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public List<String> getProductionsOfNonterminal(String nonterminal) {
        return this.productions.get(nonterminal);
    }

    public String productionsOfNonterminalToString(String nonterminal) {
        StringBuilder result = new StringBuilder();

        if(!this.nonTerminals.contains(nonterminal))
            return "\nSpecified nonterminal is not part of the grammar";

        for(String rightSide : getProductionsOfNonterminal(nonterminal))
            result.append("\n").append(nonterminal).append(" -> ").append(rightSide);

        return result.toString();
    }

    public String productionsToString() {
        StringBuilder result = new StringBuilder();

        List<String> keys = new ArrayList<String>(this.productions.keySet()).stream().sorted((a, b) -> a.compareTo(b)).toList();
        for(String leftSide : keys) {
            for(String rightSide : this.productions.get(leftSide))
                result.append("\n").append(leftSide).append(" -> ").append(rightSide);
        }

        return result.toString();
    }
}
