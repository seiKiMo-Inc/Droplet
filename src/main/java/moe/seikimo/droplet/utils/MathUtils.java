package moe.seikimo.droplet.utils;

public interface MathUtils {
    /**
     * Returns the logarithm of a number with a specified base.
     *
     * @param a The number to find the logarithm of.
     * @param newBase The base of the logarithm.
     * @return The logarithm of the number with the specified base.
     */
    static double log(double a, double newBase) {
        return Math.log(a) / Math.log(newBase);
    }

    /**
     * Returns the logarithm of a number with base 2.
     *
     * @param a The number to find the logarithm of.
     * @return The logarithm of the number with base 2.
     */
    static double log2(double a) {
        return MathUtils.log(a, 2);
    }
}
