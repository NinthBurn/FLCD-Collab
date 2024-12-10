package adt;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeTable {
    ParseTreeNode root;

    public ParseTreeTable(String rootSymbol) {
        this.root = new ParseTreeNode(rootSymbol, null);
    }

    public ParseTreeNode getRoot() {
        return root;
    }

    public void setRoot(ParseTreeNode root) {
        this.root = root;
    }

    public void getLeavesRec(ParseTreeNode node, List<ParseTreeNode> leaves) {
        for(var child : node.getChildren()) {
            if(child.isLeaf())
                leaves.add(child);

            else getLeavesRec(child, leaves);
        }

        if(node.getChildren().isEmpty())
            leaves.add(node);
    }

    public List<ParseTreeNode> getLeaves() {
        List<ParseTreeNode> leaves = new ArrayList<>();

        getLeavesRec(root, leaves);
        return leaves;
    }

    @Override
    public String toString() {
        return "ParseTreeTable{\n" +
                root.toStringSimple(0, new ArrayList<>()) +
                "\n}";
    }
}
