package ru.nsu.tumilevich;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SubstringFinder {

    public static List<Long> find(String filename, String pattern) throws IOException {
        if (pattern == null || pattern.isEmpty()) {
            return List.of();
        }

        int patternLength = pattern.length();
        if (patternLength == 0) {
            return List.of();
        }

        List<Long> result = new ArrayList<>();
        long globalCharPosition = 0;
        long globalCodePointCount = 0;
        String overlap = "";

        final int BUFFER_SIZE = 8192;
        char[] buffer = new char[BUFFER_SIZE];

        try (Reader reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8)) {
            int charsRead;
            while ((charsRead = reader.read(buffer, 0, BUFFER_SIZE)) != -1) {
                String chunk = new String(buffer, 0, charsRead);
                String block = overlap + chunk;
                int blockLength = block.length();

                if (charsRead == BUFFER_SIZE && blockLength > 0) {
                    char lastChar = block.charAt(blockLength - 1);
                    if (Character.isHighSurrogate(lastChar) && reader.ready()) {
                        int extraChar = reader.read();
                        if (extraChar != -1) {
                            block += (char) extraChar;
                            blockLength++;
                        }
                    }
                }

                int[] charToCodePoint = new int[blockLength + 1];
                int codePointIndex = 0;
                for (int i = 0; i < blockLength; ) {
                    int codePoint = block.codePointAt(i);
                    int charCount = Character.charCount(codePoint);
                    charToCodePoint[i] = codePointIndex;
                    i += charCount;
                    codePointIndex++;
                }
                charToCodePoint[blockLength] = codePointIndex;
                int desiredOverlapLength = Math.max(patternLength - 1, 1);
                int safeOverlapStart = blockLength - desiredOverlapLength;
                if (safeOverlapStart < 0) safeOverlapStart = 0;

                if (safeOverlapStart > 0 && safeOverlapStart < blockLength) {
                    if (Character.isLowSurrogate(block.charAt(safeOverlapStart))) {
                        safeOverlapStart--;
                    } else if (Character.isHighSurrogate(block.charAt(safeOverlapStart - 1)) &&
                            Character.isLowSurrogate(block.charAt(safeOverlapStart))) {
                        safeOverlapStart--;
                    }
                }

                int nonOverlapLength = safeOverlapStart;
                overlap = block.substring(safeOverlapStart);

                int index = 0;
                while ((index = block.indexOf(pattern, index)) != -1) {
                    if (index + patternLength <= nonOverlapLength + overlap.length() && index < nonOverlapLength) {
                        long matchCodePointPosition = globalCodePointCount + charToCodePoint[index];
                        result.add(matchCodePointPosition);
                    }
                    index++;
                }

                globalCharPosition += nonOverlapLength;
                globalCodePointCount += charToCodePoint[nonOverlapLength];
            }

            if (!overlap.isEmpty()) {
                int overlapLength = overlap.length();
                int[] charToCodePoint = new int[overlapLength + 1];
                int codePointIndex = 0;
                for (int i = 0; i < overlapLength; ) {
                    int codePoint = overlap.codePointAt(i);
                    int charCount = Character.charCount(codePoint);
                    charToCodePoint[i] = codePointIndex;
                    i += charCount;
                    codePointIndex++;
                }
                charToCodePoint[overlapLength] = codePointIndex;

                int index = 0;
                while ((index = overlap.indexOf(pattern, index)) != -1) {
                    long matchCodePointPosition = globalCodePointCount + charToCodePoint[index];
                    result.add(matchCodePointPosition);
                    index++;
                }
            }
        } catch (FileNotFoundException e) {
            throw new IOException("File not found: " + filename, e);
        }

        return result;
    }
}