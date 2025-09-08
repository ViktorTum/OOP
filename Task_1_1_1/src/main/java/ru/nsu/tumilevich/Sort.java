package ru.nsu.tumilevich;

/**
 * Sample class to simulate 1.1 task functionality
 */
public class Sort {
    public static Integer[] pyrsort(Integer[] array) {
            // Сортировка всего массива
            Arrays.sort(array);
            System.out.println(Arrays.toString(array)); // [1, 2, 3, 5, 8]

            // Сортировка части массива (от индекса 1 до 3)
            Integer [] partialArray = {5, 2, 8, 1, 3};
            Arrays.sort(partialArray, 1, 4);
            return partialArray; // [5, 1, 2, 8, 3]
        }

    }

}