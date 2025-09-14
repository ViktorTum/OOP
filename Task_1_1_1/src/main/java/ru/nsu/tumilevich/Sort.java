package ru.nsu.tumilevich;
import java.util.Arrays;

/**
 * Sample class to simulate 1.1 task functionality
 */
public class Sort {
    public static Integer[] heapsort(Integer[] array) {
        // Сортировка всего массива
        //построение макс кучи [5, 2, 8, 1, 3]
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
    public static Integer[] heapify(Integer[] array, int i, int ln) {
        int largest = i;
        int left = 2*i + 1;
        int right = 2*i + 2;

        if (left < ln && array[left] > array[largest]) {
            largest = left;
        }

        if (right < ln && array[right] > array[largest]) {
            largest = right;
        }

        if  (largest != i) {
            int tmp = array[largest];
            array[largest] = array[i];
            array[i] = tmp;
            array = heapify(array, largest, ln);
        }
        return array;
    }

    public static void main(String[] args) {
        Integer [] partialArray = {0};
        System.out.println(Arrays.toString(heapsort(partialArray)));
    }
//{7, 6, 2, 5, 8, 3, 1, 4};
// {5, 3, 1, 9, 6, 2, 8, 4, 7};
}