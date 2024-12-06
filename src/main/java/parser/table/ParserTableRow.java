package parser.table;

import java.util.*;

public class ParserTableRow {
    private final Integer stateIndex;
    private ParserTableAction action;

    private Map<String, Integer> goToColumns;

    public ParserTableRow(Integer stateIndex) {
        this.stateIndex = stateIndex;
        this.goToColumns = new HashMap<>();
    }

    public void setAction(ParserTableAction action) {
        if(this.action != null && !this.action.equals(action))
            throw new RuntimeException("Parser table conflict: row has actions of different type; current action: " + this.action + "; new action: " + action);

        this.action = action;
    }

    public void setGoToColumn(String symbol, Integer stateIndex) {
        if(!this.goToColumns.containsKey(symbol))
            this.goToColumns.put(symbol, stateIndex);

        else if(!Objects.equals(stateIndex, this.goToColumns.get(symbol)))
            throw new RuntimeException("Parser table conflict: row has multiple values in a single goto cell for symbol \"" + symbol + "\"; current value: \"s" + this.goToColumns.get(symbol) + "\"; new value: \"s" + stateIndex + "\"");
    }

    public ParserTableAction getAction() {
        return this.action;
    }

    public Integer getGotoState(String symbol) {
        return this.goToColumns.get(symbol);
    }

    public Integer getStateIndex() {
        return this.stateIndex;
    }

    @Override
    public String toString() {
        return "ParserTableRow{" +
                "stateIndex=" + stateIndex +
                ", action=" + action.toString() +
                ", goToColumns=" + goToColumns +
                '}';
    }
}
