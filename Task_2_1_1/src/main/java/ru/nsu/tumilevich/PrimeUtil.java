package ru.nsu.tumilevich;

public class PrimeUtil {
    /**
     * Проверка числа на простоту.
     * @param number проверяемое число
     * @return true если число простое, иначе false
     */
    static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;

        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    private PrimeUtil() {
        throw new UnsupportedOperationException();
    }
}
