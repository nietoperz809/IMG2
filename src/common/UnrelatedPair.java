package common;

public final class UnrelatedPair<U, V> {

    /**
     * The first element of this <code>Pair</code>
     */
    public final U first;

    /**
     * The second element of this <code>Pair</code>
     */
    public final V second;

    /**
     * Constructs a new <code>Pair</code> with the given values.
     *
     * @param first  the first element
     * @param second the second element
     */
    public UnrelatedPair(U first, V second) {

        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return first + "," + second;
    }
}