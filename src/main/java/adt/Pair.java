package adt;

import java.util.Objects;

public class Pair<K, T> {
    public K first;
    public T second;

    public Pair(K k, T t){
        this.first = k;
        this.second = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public String toString() {
        return "Pair { first: " + first + ", second: " + second + " }";
    }
}
