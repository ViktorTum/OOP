package ru.nsu.tumilevich;

import java.util.Objects;
import java.util.Scanner;

/**
 * BlackjackMain - главный класс для запуска игры в Блэкджек.
 * Содержит основной игровой цикл и логику раундов.
 */
public class BlackjackMain {
	public static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("Добро пожаловать в Блэкджек!");
		int roundNum = 0;
		int playerWins = 0;
		int casinoWins = 0;
		String answer = "Yes";

		while (Objects.equals(answer, "Yes")) {
			roundNum++;
			int roundResult = Raund(roundNum);
			if (roundResult == 1) {
				playerWins++;
				System.out.printf("Вы выиграли раунд! Счет %d:%d\n", playerWins, casinoWins);
			} else {
				casinoWins++;
				System.out.printf("Вы проиграли раунд( Счет %d:%d\n", playerWins, casinoWins);
			}
			System.out.println("Продолжаем? (Yes/No)");
			answer = scanner.nextLine();
		}
		System.out.println("Спасибо за игру!");
	}

	/**
	 * Проводит один раунд игры.
	 * @param n Номер текущего раунда.
	 * @return 1 - победа игрока, 0 - победа крупье.
	 */
	static int Raund(int n) {
		/* Инициализация раунда */
		Player player = new Player();
		Krupie krupie = new Krupie();
		Deck deck = new Deck(); // Конструктор сам создаст и перемешает колоду

		/* Начало игры */
		System.out.println("\nРаунд " + n);
		System.out.println("Дилер раздал карты.");

		player.TakeCard(deck);
		player.TakeCard(deck);
		System.out.print("\tВаши карты: ");
		player.ShowMyCards();

		if (player.summCard == 21) {
			return 1; /* Блэкджек у игрока */
		}
		if (player.summCard > 21) {
			return 0; /* Перебор у игрока */
		}

		krupie.TakeCard(deck);
		System.out.print("\tКарты дилера: ");
		krupie.ShowMyCards();

		/* Ход игрока */
		while (true) {
			System.out.println("Ваш ход (Take - взять карту, Pass - пас):");
			String playerAction = scanner.nextLine();

			if (Objects.equals(playerAction, "Take")) {
				System.out.printf("\tВы получили - %s\n", player.TakeCard(deck));
				System.out.print("\tВаши карты: ");
				player.ShowMyCards();

				if (player.summCard == 21) {
					return 1; /* Победа */
				}
				if (player.summCard > 21) {
					return 0; /* Перебор */
				}
			} else if (Objects.equals(playerAction, "Pass")) {
				System.out.println("\nХод дилера:");
				int dillerResult = krupie.DillerTurn(deck, player); // Упрощенный вызов
				return (dillerResult == 1) ? 0 : 1; /* Если дилер выиграл (1), то игрок проиграл (0) */
			}
		}
	}
}
