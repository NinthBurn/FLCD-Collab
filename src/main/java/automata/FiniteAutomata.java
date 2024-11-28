package automata;

import adt.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FiniteAutomata {
    private List<String> states;
    private List<String> alphabet;
    private String initialState;
    private List<String> finalStates;
    private final Map<Pair<String, String>, List<String>> transitions;
    private final String separator = " ";

    public FiniteAutomata(String filename) throws Exception{
        this.states = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.initialState = "";
        this.finalStates = new ArrayList<>();
        this.transitions = new HashMap<>();

        extractFromFile(filename);
    }

    private void extractFromFile(String filename) throws Exception{
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();

            this.states = List.of(line.trim().split(separator));

            line = reader.readLine();
            this.alphabet = List.of(line.trim().split(separator));

            line = reader.readLine();
            if(line.split(separator).length > 1)
                throw new Exception("More than one initial state");

            this.initialState = line.trim();

            line = reader.readLine();
            this.finalStates = List.of(line.trim().split(separator));

            line = reader.readLine();
            while (line != null) {
                String[] transition = line.split(separator);

                if(transition.length > 3)
                    throw new Exception("Invalid transition");

                String stateFrom = transition[0];
                String symbol = transition[1];
                String stateTo = transition[2];

                if(!this.states.contains(stateFrom) || !this.states.contains(stateTo))
                    throw new Exception("One of the states was not declared");

                if(!this.alphabet.contains(symbol))
                    throw new Exception("Transition symbol does not belong to the alphabet");

                Pair<String, String> stateSymbol = new Pair<>(stateFrom, symbol);
                if(!this.transitions.containsKey(stateSymbol)) {
                    List<String> toStates = new ArrayList<>();
                    toStates.add(stateTo);
                    this.transitions.put(stateSymbol, toStates);

                } else {
                    this.transitions.get(stateSymbol).add(stateTo);
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean isDeterministic() {
        return this.transitions.values().stream().allMatch(list -> list.size() <= 1);
    }

    public boolean acceptsSequence(String sequence) {
        if (!this.isDeterministic()) {
            return false;
        }

        if(sequence.isEmpty())
            return finalStates.contains(initialState);

        String currentState = this.initialState;

        int length = sequence.length();
        for (int i = 0; i < length; ++i) {
            String currentSymbol = sequence.substring(i, i + 1);

            Pair<String, String> transition = new Pair<>(currentState, currentSymbol);

            if (!this.transitions.containsKey(transition)) {
                return false;
            } else {
                currentState = this.transitions.get(transition).get(0);
            }
        }

        return this.finalStates.contains(currentState);
    }

    public List<String> getStates() {
        return states;
    }

    public List<String> getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public List<String> getFinalStates() {
        return finalStates;
    }

    public String transitionsToString() {
        StringBuilder result = new StringBuilder();

        for(Pair<String, String> symbolState : this.transitions.keySet()) {
            result.append("δ(").append(symbolState.first).append(", ").append(symbolState.second).append(") = ").append(this.transitions.get(symbolState)).append("\n");
        }

        return result.toString();
    }
}
