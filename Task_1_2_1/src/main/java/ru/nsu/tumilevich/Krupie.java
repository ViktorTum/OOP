package ru.nsu.tumilevich;

/**
 * Класс Krupie представляет дилера (крупье).
 * Наследуется от Player и реализует специфическую логику хода дилера.
 */
public class Krupie extends Player {

	/**
	 * Реализует логику хода дилера. Дилер берет карты, пока сумма меньше 17
	 * или меньше, чем у игрока.
	 * @param deck   Игровая колода.
	 * @param player Экземпляр игрока.
	 * @return 1 если дилер выиграл, 0 если проиграл.
	 */
	public Integer DillerTurn(Deck deck, Player player) {
		while (this.summCard < 17 || this.summCard < player.summCard) {
			System.out.printf("\tКрупье получает карту - %s, сумма - %s\n", this.TakeCard(deck), this.summCard);
		}
		if (this.summCard > 21) {
			return 0; /* Перебор у дилера, дилер проиграл */
		}
		if (this.summCard >= player.summCard) {
			return 1; /* Дилер выиграл */
		}
		return 0; /* Дилер проиграл (сумма меньше, чем у игрока) */
	}
}
