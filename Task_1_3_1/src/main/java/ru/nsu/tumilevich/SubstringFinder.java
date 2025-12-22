package ru.nsu.tumilevich;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SubstringFinder {

    public static List<Long> find(String filename, String pattern) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filename);
        }

        if (pattern == null || pattern.isEmpty()) {
            return List.of();
        }

        //TODO read stream
        String content = Files.readString(path, StandardCharsets.UTF_8);
        int n = content.length();
        int m = pattern.length();

        if (m > n) {
            return List.of();
        }

        int[] charIndexToCodePointIndex = new int[n];
        Arrays.fill(charIndexToCodePointIndex, -1);

        int currentCodePointIndex = 0;
        for (int i = 0; i < n; ) {
            int codePoint = content.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            charIndexToCodePointIndex[i] = currentCodePointIndex;
            i += charCount;
            currentCodePointIndex++;
        }

        List<Long> result = new ArrayList<>();
        int index = 0;
        while (index <= n - m) {
            index = content.indexOf(pattern, index);
            if (index == -1) {
                break;
            }

            if (charIndexToCodePointIndex[index] == -1) {
                index++;
                continue;
            }

            result.add((long) charIndexToCodePointIndex[index]);
            index += m;
        }

        return result;
    }

    private SubstringFinder() {
        throw new UnsupportedOperationException();
    }
}