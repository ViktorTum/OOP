package ru.nsu.tumilevich;

import java.util.Arrays;

/**
 * Класс для реализации пирамидальной сортировки.
 */
public class Sort {

    /**
     * Реализация алгоритма пирамидальной сортировки.
     *
     * @param array массив для сортировки.
     * @return отсортированный массив.
     */
    @SuppressWarnings("checkstyle:Indentation")
    public static Integer[] heapsort(Integer[] array) {
        int n = array.length;

        for (int i = n / 2; i >= 0; i--) {
            array = heapify(array, i, n);
        }

        for (int i = n - 1; i > 0; i--) {
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            array = heapify(array, 0, i);
        }

        return array;
    }

    /**
     * Вспомогательный метод для построения кучи.
     *
     * @param array массив.
     * @param i     индекс текущего узла.
     * @param ln    размер кучи.
     * @return измененный массив.
     */
    @SuppressWarnings("checkstyle:Indentation")
    public static Integer[] heapify(Integer[] array, int i, int ln) {
        int largest = i;
        int left = 2 * i + 1;  // <-- ИСПРАВЛЕНО: пробелы вокруг '*'
        int right = 2 * i + 2; // <-- ИСПРАВЛЕНО: пробелы вокруг '*'

        if (left < ln && array[left] > array[largest]) {
            largest = left;
        }

        if (right < ln && array[right] > array[largest]) {
            largest = right;
        }

        if (largest != i) {
            int tmp = array[largest];
            array[largest] = array[i];
            array[i] = tmp;
            array = heapify(array, largest, ln);
        }
        return array;
    }

    /**
     * Точка входа в программу.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        Integer[] partialArray = {0};
        System.out.println(Arrays.toString(heapsort(partialArray)));
    }
}