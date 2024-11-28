package parser;

import java.util.Objects;

public class AnalysisItem {
    private final String leftHandSide;
    private final String rightHandSide;
    private int cursor;

    public AnalysisItem(String leftHandSide, String rightHandSide) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.cursor = 0;
    }

    public AnalysisItem(String leftHandSide, String rightHandSide, int dotPosition) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.cursor = dotPosition;
    }
    
    public String getLeftHandSide() {
        return leftHandSide;
    }

    public String getRightHandSide() {
        return rightHandSide;
    }
    public int getCursor() {
        return cursor;
    }

    public String getSymbolAtCursor() {
        if(cursorAtEnd())
            return "";

        return rightHandSide.split(" ")[cursor];
    }

    public AnalysisItem moveCursorRight() {
        if (cursor < rightHandSide.split(" ").length)
            return new AnalysisItem(leftHandSide, rightHandSide, cursor + 1);

        return this;
    }

    public boolean cursorAtEnd() {
        return cursor == rightHandSide.split(" ").length;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AnalysisItem item = (AnalysisItem) obj;
        return cursor == item.getCursor() && leftHandSide.equals(item.getLeftHandSide()) && rightHandSide.equals(item.getRightHandSide());
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftHandSide, rightHandSide, cursor);
    }

    @Override
    public String toString() {
        String[] rightHandsSides = rightHandSide.split(" ");
        StringBuilder result = new StringBuilder(leftHandSide + " -> ");

        for(int index = 0; index < rightHandsSides.length; ++index) {
            if(cursor == index)
                result.append(".");

            result.append(rightHandsSides[index]).append(" ");
        }

        if(cursorAtEnd())
            result.append(".");

        return result.toString();
    }
}
