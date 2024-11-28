package adt;

public class SymbolTable {
    private final HashTable<String> hashTable;

    public SymbolTable(int size) {
        this.hashTable = new HashTable<>(size);
    }

    public boolean add(String value) {
        return this.hashTable.add(value);
    }

    public boolean add(Integer value) {
        return this.hashTable.add(String.valueOf(value));
    }

    public boolean delete(String value) {
        return this.hashTable.delete(value);
    }

    public boolean delete(Integer value) {
        return this.hashTable.delete(String.valueOf(value));
    }

    public String findSymbol(Pair<Integer, Integer> position) {
        return this.hashTable.getValue(position);
    }

    public Pair<Integer, Integer> findPosition(String value) {
        return this.hashTable.getPosition(value);
    }

    public Pair<Integer, Integer> findPosition(Integer value) {
        return this.hashTable.getPosition(String.valueOf(value));
    }

    public int getSize() {
        return this.hashTable.getSize();
    }

    public String toString() {
        return "Symbol Table { " + this.hashTable.toString() + " }";
    }
}
