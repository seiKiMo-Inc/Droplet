package moe.seikimo.droplet.utils.objects.binary;

import moe.seikimo.droplet.utils.Preconditions;

public final class SimpleBitArray implements BitArray {
    private static final int[] MAGIC = new int[]{-1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE, 0, 5};

    private final int bits, mask, size, valuesPerLong;
    private final int multiply, add, shift;
    private final long[] data;

    public SimpleBitArray(int bits, int size, long[] data) {
        this.bits = bits;
        this.size = size;

        this.mask = (1 << bits) - 1;
        this.valuesPerLong = (char) (64 / bits);

        var offset = 3 * (this.valuesPerLong - 1);
        this.multiply = MAGIC[offset];
        this.add = MAGIC[offset + 1];
        this.shift = MAGIC[offset + 2];

        var words = (size + this.valuesPerLong - 1) / this.valuesPerLong;
        if (data != null) {
            if (data.length != words)
                throw new IllegalArgumentException("Invalid length given for storage, got: " + data.length + " but expected: " + words);

            this.data = data;
        } else {
            this.data = new long[words];
        }
    }

    /**
     * Currently unknown.
     *
     * @param index Unknown.
     * @return Unknown.
     */
    private int index(int index) {
        var multiply = Integer.toUnsignedLong(this.multiply);
        var add = Integer.toUnsignedLong(this.add);
        return (int) ((long) index * multiply + add >> 32 >> this.shift);
    }

    @Override
    public void set(int index, int value) {
        Preconditions.inclusive(0, this.size - 1, index);
        Preconditions.inclusive(0, this.mask, value);

        var longIndex = this.index(index);
        var currentValue = this.data[longIndex];

        var bitIndex = (index - longIndex * this.valuesPerLong) * this.bits;
        this.data[longIndex] = currentValue & ~((long) this.mask << bitIndex)
                | ((long) value & this.mask) << bitIndex;
    }

    @Override
    public int get(int index) {
        Preconditions.inclusive(0, this.size - 1, index);

        var longIndex = this.index(index);
        var currentValue = this.data[longIndex];

        var bitIndex = (index - longIndex * this.valuesPerLong) * this.bits;
        return (int) (currentValue >> bitIndex & this.mask);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public long[] getWords() {
        return this.data;
    }

    @Override
    public BitArrayVersion getVersion() {
        return null;
    }

    @Override
    public BitArray copy() {
        return new SimpleBitArray(this.bits, this.size, this.data.clone());
    }
}
