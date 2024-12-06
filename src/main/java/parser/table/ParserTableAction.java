package parser.table;

import java.util.Objects;

public class ParserTableAction {
    public ParserTableActionType actionType;
    public Integer productionIndex;

    public ParserTableAction(ParserTableActionType actionType) {
        this.actionType = actionType;
        productionIndex = -1;

        if(actionType == ParserTableActionType.REDUCE)
            throw new RuntimeException("Reduce action must have an associated production index");
    }

    public ParserTableAction(ParserTableActionType actionType, Integer productionIndex) {
        this.actionType = actionType;
        this.productionIndex = productionIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParserTableAction that = (ParserTableAction) o;
        return actionType == that.actionType && Objects.equals(productionIndex, that.productionIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionType, productionIndex);
    }

    @Override
    public String toString() {
        return "ParserTableAction{" +
                "actionType=" + actionType +
                ", productionIndex=" + productionIndex +
                '}';
    }
}
