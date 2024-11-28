package adt;

import java.util.ArrayList;

public class HashTable<K> {
    private final int size;
    private final ArrayList<ArrayList<K>> buckets;

    public HashTable(int size){
        this.size = size;
        this.buckets = new ArrayList<>(size);

        for(int i = 0; i < size; ++i)
            this.buckets.add(new ArrayList<K>());
    }

    public boolean add(K value) {
        int pos = Math.abs(value.hashCode() % this.size);

        if(this.buckets.get(pos).contains(value))
            return false;

        return this.buckets.get(pos).add(value);
    }

    public boolean contains(K value) {
        int pos = Math.abs(value.hashCode() % this.size);

        return this.buckets.get(pos).contains(value);
    }

    public boolean containsOnPosition(Pair<Integer, Integer> position) {
        int bucketPos = position.first, pos = position.second;
        if(bucketPos >= this.size)
            return false;

        ArrayList<K> bucket = this.buckets.get(bucketPos);

        return !(pos >= bucket.size());
    }

    public boolean delete(K value) {
        int pos = value.hashCode() % this.size;

        return this.buckets.get(pos).remove(value);
    }

    public boolean deleteOnPosition(Pair<Integer, Integer> position) {
        int bucketPos = position.first, pos = position.second;

        if(bucketPos >= this.size)
            return false;

        ArrayList<K> bucket = this.buckets.get(bucketPos);

        if(pos >= bucket.size())
            return false;

        bucket.remove(pos);
        return true;
    }

    public Pair<Integer, Integer> getPosition(K value) {
        int bucketPos = Math.abs(value.hashCode() % this.size);
        int pos = this.buckets.get(bucketPos).indexOf(value);

        if(pos == -1)
            return null;

        return new Pair<Integer, Integer>(bucketPos, pos);
    }

    public K getValue(Pair<Integer, Integer> position) {
        int bucketPos = position.first, pos = position.second;

        if(bucketPos >= this.size)
            return null;

        ArrayList<K> bucket = this.buckets.get(bucketPos);

        if(pos >= bucket.size())
            return null;

        return bucket.get(pos);
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder("Hash Table { size: " + this.size + ", buckets: \n");
        for(int i = 0; i < this.size; ++i){
            result.append("\t[").append(i).append("] { ");

            ArrayList<K> bucket = this.buckets.get(i);
            for(int j = 0; j < bucket.size(); ++j)
                result.append(bucket.get(j)).append(", ");

            result.append(" }\n");
        }

        result.append(" }");
        return result.toString();
    }
}
