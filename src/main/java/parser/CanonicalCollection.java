package parser;

import java.util.ArrayList;
import java.util.List;

public class CanonicalCollection {
    private List<State> states;

    public CanonicalCollection() {
        states = new ArrayList<>();
    }

    public void addState(State newState) {
        states.add(newState);
    }

    public List<State> getStates() {
        return states;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("CanonicalCollection{\n" + "\tstates = {\n");

        for(int index = 0; index < states.size(); ++index)
            result.append("\t state ").append(index).append(" ").append(states.get(index).toString()).append("\n");

        return result.append("\t}\n}").toString();
    }
}
