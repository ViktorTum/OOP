package ru.nsu.tumilevich;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SubstringFinderTest {

	private final String testFileName = "test_input.txt";
	private Path testFilePath;

	@BeforeEach
	void setUp() {
		testFilePath = Paths.get(testFileName);
	}

	@AfterEach
	void tearDown() throws IOException {
		Files.deleteIfExists(testFilePath);
	}

	/**
	 * Вспомогательный метод для создания тестового файла с нужным содержимым.
	 */
	private void createTestFile(String content) throws IOException {
		Files.writeString(testFilePath, content, StandardCharsets.UTF_8);
	}

	@Test
	void testSimpleCase() throws IOException {
		createTestFile("абракадабра");
		List<Long> expected = List.of(1L, 8L);
		List<Long> actual = SubstringFinder.find(testFileName, "бра");
		assertEquals(expected, actual, "Должен найти два вхождения в простом случае");
	}

	@Test
	void testNoOccurrences() throws IOException {
		createTestFile("абракадабра");
		List<Long> expected = List.of(); // Пустой список
		List<Long> actual = SubstringFinder.find(testFileName, "xyz");
		assertEquals(expected, actual, "Не должен находить вхождений, если их нет");
	}

	@Test
	void testEmptyFile() throws IOException {
		createTestFile("");
		List<Long> expected = List.of();
		List<Long> actual = SubstringFinder.find(testFileName, "а");
		assertEquals(expected, actual, "Должен возвращать пустой список для пустого файла");
	}

	@Test
	void testEmptySubstring() throws IOException {
		createTestFile("абракадабра");
		List<Long> expected = List.of();
		List<Long> actual = SubstringFinder.find(testFileName, "");
		assertEquals(expected, actual, "Должен возвращать пустой список, если подстрока пуста");
	}

	@Test
	void testOverlapCase() throws IOException {
		int bufferSize = 8192;
		String part1 = "x".repeat(bufferSize - 1);
		String part2 = "by";
		createTestFile(part1 + "a" + part2);

		List<Long> expected = List.of((long)bufferSize - 1);
		List<Long> actual = SubstringFinder.find(testFileName, "ab");

		assertEquals(expected, actual, "Должен находить вхождение на границе буферов");
	}
}
