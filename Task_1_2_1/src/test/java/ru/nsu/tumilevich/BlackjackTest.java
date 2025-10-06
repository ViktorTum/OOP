package ru.nsu.tumilevich;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

/**
 * Тесты для классов Deck, Player и Krupie.
 */
class BlackjackTest {

	@Test
	void DeckTest() {
		Deck deck = new Deck(); // Колода уже создана и перемешана
		String[] allDeck = deck.SeeAllCards();
		for (int i = 0; i < 52; i++) {
			boolean isPresent = Arrays.asList(allDeck).contains(deck.GiveFirstCard());
			assertTrue(isPresent);
		}
		assertNull(deck.GiveFirstCard());
	}

	@Test
	void PlayerZeroCardTest() {
		Player player = new Player();
		assertEquals(0, player.ShowMyCards());
	}

	@Test
	void PlayerNCardTest() {
		Deck deck = new Deck();
		String[] allDeck = deck.SeeAllCards(); // Получаем уже перемешанную колоду

		Player player = new Player();
		assertEquals(0, player.ShowMyCards());

		// Тест теперь проверяет, что игрок может взять все 52 карты
		for (int i = 0; i < 52; i++) {
			String card = player.TakeCard(deck);
			assertTrue(Arrays.asList(allDeck).contains(card));
		}
		player.ShowMyCards();
	}

	@Test
	void KrupieTest() {
		for (int i = 0; i < 10; i++) {
			Deck deck = new Deck(); // Новая колода для каждого теста
			Krupie krupie = new Krupie();
			Player player = new Player();
			// Дадим игроку какие-то карты для осмысленного теста
			player.TakeCard(deck);
			player.TakeCard(deck);

			// Если у игрока не перебор, запускаем ход дилера
			if (player.summCard <= 21) {
				int gameStat = krupie.DillerTurn(deck, player);

				if (krupie.summCard > 21) {
					assertEquals(0, gameStat); // Дилер проиграл
				} else if (krupie.summCard >= player.summCard) {
					assertEquals(1, gameStat); // Дилер выиграл
				} else {
					assertEquals(0, gameStat); // Дилер проиграл
				}
			}
		}
	}
}
