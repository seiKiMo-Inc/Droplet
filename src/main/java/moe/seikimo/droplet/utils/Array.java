package moe.seikimo.droplet.utils;

import java.util.ArrayList;
import java.util.List;

public interface Array {
    /**
     * Returns an array list with a single value.
     *
     * @param value The value to add to the list.
     * @return A list with a single value.
     * @param <T> The type of the value.
     */
    static <T> List<T> single(T value) {
        return new ArrayList<>() {{
            this.add(value);
        }};
    }

    /**
     * Returns an array list with a specified size and value.
     *
     * @param size The size of the list.
     * @param value The value to add to the list.
     * @return A list with the specified size and value.
     * @param <T> The type of the value.
     */
    static <T> List<T> fill(int size, T value) {
        return new ArrayList<>() {{
            for (int i = 0; i < size; i++) {
                this.add(value);
            }
        }};
    }
}
