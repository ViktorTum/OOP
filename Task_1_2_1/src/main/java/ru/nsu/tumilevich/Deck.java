package ru.nsu.tumilevich;

import java.util.Random;

/**
 * Класс Deck представляет собой игральную колоду карт.
 * Он отвечает за создание, перемешивание и раздачу карт.
 */
public class Deck {

	private final String[] deck = new String[52];
	private int cardNow = 0; // карта, которую вытягивают

	private final String[] suits = {"пик", "червей", "бубен", "треф"};
	private final String[] ranks = {"Туз", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Валет", "Дама", "Король"};

	private Random rand = new Random();
	/**
	 * Конструктор класса Deck.
	 * Автоматически создает и перемешивает колоду при создании объекта.
	 */
	public Deck() {
		this.makeDeck();
		this.updateDeck();
	}

	/**
	 * Создает стандартную колоду из 52 карт.
	 */
	private void makeDeck() {
		int cardIndex = 0; // Используем локальный индекс для заполнения

		for (String suit : suits) {
			for (String rank : ranks) {
				this.deck[cardIndex++] = rank + " " + suit;
			}
		}
		this.cardNow = 0; // Сбрасываем счетчик текущей карты для раздачи
	}

	/**
	 * Перемешивает колоду в случайном порядке.
	 */
	private void updateDeck() {

		for (int i = this.deck.length - 1; i > 0; i--) {
			int j = rand.nextInt(i + 1);
			String temp = this.deck[i];
			this.deck[i] = this.deck[j];
			this.deck[j] = temp;
		}
	}

	/**
	 * Выдает одну карту с верха колоды.
	 * @return Строка с названием карты или null, если карты закончились.
	 */
	public String giveFirstCard() {
		if (this.cardNow < 52) {
			return this.deck[this.cardNow++];
		}
		return null;
	}

	/**
	 * Возвращает все карты в колоде (для тестов).
	 * @return Массив строк с картами.
	 */
	public String[] SeeAllCards() {
		return this.deck;
	}
}
