package tests;

import adt.Pair;
import adt.SymbolTable;

public class SymbolTableTest {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(10);

        symbolTable.add("id1");
        symbolTable.add("id2");
        symbolTable.add("id3");

        symbolTable.add(3);
        symbolTable.add(15);
        symbolTable.add(-12);

        System.out.println(symbolTable.toString() + "\n");

        System.out.println(symbolTable.findPosition(3));
        System.out.println(symbolTable.findPosition(2));
        System.out.println(symbolTable.findPosition(-12));

        System.out.println();

        System.out.println(symbolTable.findPosition("id1"));
        System.out.println(symbolTable.findPosition("id4"));

        System.out.println();

        System.out.println(symbolTable.findSymbol(new Pair<>(1,1)));
        System.out.println(symbolTable.findSymbol(new Pair<>(1,2)));

        System.out.println();

        System.out.println(symbolTable.findSymbol(new Pair<>(4,0)));
        System.out.println(symbolTable.findSymbol(new Pair<>(4,1)));

        System.out.println();

        symbolTable.delete(15);
        symbolTable.delete("id2");

        System.out.println(symbolTable.toString());
        System.out.println(symbolTable.findSymbol(new Pair<>(2,0)));
        System.out.println(symbolTable.findSymbol(new Pair<>(5,0)));

    }
}
