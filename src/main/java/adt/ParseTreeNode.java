package adt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParseTreeNode {
    String symbol;
    ParseTreeNode parent;
    // from left to right - the order of insertion
    List<ParseTreeNode> children;

    public ParseTreeNode(String symbol, ParseTreeNode parent) {
        this.symbol = symbol;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public ParseTreeNode(String symbol) {
        this.symbol = symbol;
        this.children = new ArrayList<>();
    }

    public String getSymbol() {
        return symbol;
    }

    public ParseTreeNode getParent() {
        return parent;
    }

    public List<ParseTreeNode> getChildren() {
        return children;
    }

    public void addChild(ParseTreeNode child) {
        child.parent = this;
        children.add(child);
    }

    public void removeChild(ParseTreeNode child) {
        children.remove(child);
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParseTreeNode that = (ParseTreeNode) o;
        return Objects.equals(symbol, that.symbol) && Objects.equals(parent, that.parent) && Objects.equals(children.size(), that.children.size());
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, parent);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int depth) {
        // to avoid infinite recursion
        int maxDepth = 9000;
        if (depth > maxDepth) {
            return "ParseTreeNode " +
                    "symbol='" + symbol + '\'' +
                    ", parent=" + (parent != null ? parent.getSymbol() : "null") +
                    ", children=[...]";
        }

        String parentSymbol = (parent != null) ? parent.getSymbol() : "null";

        String indent = String.join("", Collections.nCopies(depth, "\t"));

        String childrenDetails = children.isEmpty() ? "[]" :
                "\n" + children.stream()
                        .map(child -> indent + child.toString(depth + 1))
                        .collect(Collectors.joining(",\n"));

        return indent + "ParseTreeNode " +
                "symbol='" + symbol + '\'' +
                ", parent=" + parentSymbol +
                ", children=" + childrenDetails;
    }

    public String toStringSimple(int depth, List<Integer> previousLines) {
        int maxDepth = 9000;
        if (depth > maxDepth) {
            return symbol + " - [...]";
        }

//        String indent = String.join("", Collections.nCopies(previousLines, "|   ")) + String.join("", Collections.nCopies(depth - previousLines, "    "));
        String indent = "";
        for(int i = 0; i < depth; ++i) {
            if(previousLines.contains(i))
                indent += "|   ";
            else indent += "    ";
        }

        if(!previousLines.contains(depth))
            previousLines.add(depth);

        String result = symbol + "\n";

        for (int i = 0; i < children.size(); i++) {
            ParseTreeNode child = children.get(i);

            if(i == children.size() - 1) {
                previousLines.removeIf(a -> a == depth);
                result += indent + "|\n" + indent + "└─> " + child.toStringSimple(depth + 1, previousLines);
            }
            else
                result += indent + "|\n" + indent + "├─> " + child.toStringSimple(depth + 1, previousLines);

        }

        return result;
    }

}
