package parser;

import adt.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanonicalCollection {
    private final List<State> states;
    private final Map<Pair<Integer, String>, Integer> stateSymbolToState;
    private AnalysisItem acceptState;

    public CanonicalCollection() {
        states = new ArrayList<>();
        stateSymbolToState = new HashMap<>();
    }

    public void addState(State newState) {
        states.add(newState);
    }

    public List<State> getStates() {
        return states;
    }

    public void connectStateToAnother(Integer stateFrom, String symbol, Integer stateTo) {
        stateSymbolToState.put(
                new Pair<>(stateFrom, symbol),
                stateTo
        );
    }

    public Map<Pair<Integer, String>, Integer> getConnectedStates() {
        return this.stateSymbolToState;
    }

    public void setAcceptState(AnalysisItem acceptState) {
        this.acceptState = acceptState;
    }

    public AnalysisItem getAcceptState() {
        return acceptState;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("CanonicalCollection{\n" + "\tstates = {\n");

        for(int index = 0; index < states.size(); ++index)
            result.append("\t state ").append(index).append(" ").append(states.get(index).toString()).append("\n");

        return result.append("\t}\n}").toString();
    }
}
