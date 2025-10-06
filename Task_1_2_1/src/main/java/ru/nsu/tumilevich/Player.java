package ru.nsu.tumilevich;

/**
 * Класс Player представляет игрока в Блэкджек.
 * Хранит карты игрока, подсчитывает их сумму и управляет взятием карт.
 */
public class Player {

	private int cardCount = 0;
	public int summCard = 0;
	private int clearSumm = 0;
	private int aceCount = 0;
	private final String[] cards = new String[53]; /* 52 карты + 1 для запаса */

	/**
	 * Берет одну карту из колоды и обновляет сумму очков.
	 * @param gameDeck Колода, из которой берется карта.
	 * @return Название взятой карты.
	 */
	public String TakeCard(Deck gameDeck) {
		String card = gameDeck.GiveFirstCard();
		if (card == null) {
			System.out.println("В колоде закончились карты!");
			return "Нет карты";
		}
		cards[cardCount++] = card;

		/* Получаем ранг карты (первое слово) */
		String rank = card.split(" ")[0];

		switch (rank) {
			case "Туз":
				aceCount++;
				break;
			case "Валет", "Дама", "Король":
				clearSumm += 10;
				break;
			default:
				clearSumm += Integer.parseInt(rank);
				break;
		}

		/* Логика подсчета тузов */
		summCard = clearSumm;
		for (int i = 0; i < aceCount; i++) {
			if (summCard + 11 <= 21) {
				summCard += 11;
			} else {
				summCard += 1;
			}
		}
		return card;
	}

	/**
	 * Отображает текущие карты игрока и их сумму в консоли.
	 * @return Сумма очков игрока.
	 */
	public int ShowMyCards() {
		if (cardCount == 0) {
			System.out.print("[]");
			return summCard;
		}

		System.out.print("[");
		for (int i = 0; i < cardCount - 1; i++) {
			System.out.print(cards[i] + ", ");
		}
		System.out.print(cards[cardCount - 1]);
		System.out.print("]");
		System.out.println(" => " + summCard);

		return summCard;
	}
}
