package adt;

import parser.table.ParserTableActionType;
import parser.table.ParserTableRow;
import utils.TableBuilder;

import java.util.*;

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

    public void getNodesRec(ParseTreeNode node, List<ParseTreeNode> nodes) {
        for(var child : node.getChildren()) {
            nodes.add(child);

            getNodesRec(child, nodes);
        }
    }

    public List<ParseTreeNode> getNodes() {
        List<ParseTreeNode> nodes = new ArrayList<>();

        getNodesRec(root, nodes);
        return nodes;
    }

    @Override
    public String toString() {
        return "ParseTreeTable{\n" +
                root.toStringTree() +
                "\n}";
    }

    public String toStringTable() {
        List<ParseTreeNode> nodes = getNodes();

        List<String> headers = new ArrayList<>(List.of("CHILDREN"));
        List<String> rowNames = new ArrayList<>();
        List<List<String>> cellValues = new ArrayList<>();

        int index = 0;
        for(index = 0; index < nodes.size(); ++index)
            cellValues.add(new ArrayList<>());

        index = 0;
        for(ParseTreeNode node : nodes) {
            rowNames.add(node.getSymbol());

            String childrenString = "";
            for(var child: node.getChildren()) {
                childrenString += child.getSymbol() + " ";
            }

            cellValues.get(index).add(childrenString);
            index++;
        }

        String[][] finalData = cellValues.stream()
                .map(arr -> arr.toArray(String[]::new))
                .toArray(String[][]::new);

        return new TableBuilder()
                .setAlignment(TableBuilder.Alignment.CENTER)
                .addHeaders(headers.toArray(new String[0]))
                .addRowNames(rowNames.toArray(new String[0]))
                .setName("PARENT")
                .setValues(finalData)
                .frame(true)
                .setBorders(TableBuilder.Borders.FRAME)
                .build();
    }

    public String toStringVerbose() {
        return "ParseTreeTable{\n" +
                root.toString() +
                "\n}";
    }
}
