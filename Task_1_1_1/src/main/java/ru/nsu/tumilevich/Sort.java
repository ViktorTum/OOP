package ru.nsu.tumilevich;
import java.util.Arrays;

/**
 * Sample class to simulate 1.1 task functionality
 */
public class Sort {
    public static Integer[] pyrsort(Integer[] array) {
        // Сортировка всего массива
        Arrays.sort(array);
        return array; // [1, 2, 3, 5, 8]

    }

    public static void main(String[] args) {
        Integer [] partialArray = {5, 2, 8, 1, 3};
        System.out.println(Arrays.toString(pyrsort(partialArray)));
    }

}