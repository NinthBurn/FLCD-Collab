package parser;

import java.util.List;
import java.util.Objects;

public class State {
    private final List<AnalysisItem> items;

    public State(List<AnalysisItem> analysisItems) {
        items = analysisItems;
    }

    public List<AnalysisItem> getItems() {
        return items;
    }

    @Override
    public String toString(){
        return items.toString();
    }

    @Override
    public int hashCode(){
        return Objects.hash(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return items.equals(state.items);
    }
}
