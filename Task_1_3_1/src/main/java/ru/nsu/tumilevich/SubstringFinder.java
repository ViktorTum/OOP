package ru.nsu.tumilevich;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class SubstringFinder {

    /**
     * Находит все вхождения подстроки в файле.
     *
     * @param fileName Имя файла для поиска.
     * @param subString Подстрока для поиска.
     * @return Список индексов (типа long) начала каждого вхождения подстроки.
     * @throws IOException если возникает ошибка при чтении файла.
     */
    public static List<Long> find(String fileName, String subString) throws IOException {
        List<Long> occurrences = new ArrayList<>();
        if (subString == null || subString.isEmpty()) {
            return occurrences;
        }

        int subStringLength = subString.length();
        int bufferSize = 8192;
        char[] buffer = new char[bufferSize];

        String overlap = "";
        long filePosition = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            int charsRead;
            while ((charsRead = reader.read(buffer, 0, bufferSize)) != -1) {
                String blockToSearch = overlap + new String(buffer, 0, charsRead);

                int index = -1;
                while ((index = blockToSearch.indexOf(subString, index + 1)) != -1) {
                    occurrences.add(filePosition + index);
                }

                int overlapLength = Math.min(blockToSearch.length(), subStringLength - 1);
                overlap = blockToSearch.substring(blockToSearch.length() - overlapLength);

                filePosition += blockToSearch.length() - overlapLength;
            }
        }

        return occurrences;
    }
}
