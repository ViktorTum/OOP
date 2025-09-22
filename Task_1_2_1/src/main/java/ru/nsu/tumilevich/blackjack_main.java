package ru.nsu.tumilevich;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;


/**
 * blackjack_main- главная функция, для игры
 *
 */

class Deck {
    static String[] suits = {"пик", "червей", "бубен", "треф"};
	static String[] ranks = {"Туз", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Валет", "Дама", "Король"};

    static String[] deck = new String[52];
    static int cardIndex = 0;

	static int card_now = 0;

    //создание и перетасовка колоды
    public static void up_date_deck(){
	    //создание колоды
        cardIndex = 0;
        for (String suit : suits) {
            for (String rank : ranks) {
                deck[cardIndex++] = rank + " " + suit;
            }
        }

		//мешаю колоду
        Random rand = new Random();
	    for (int i = deck.length - 1; i > 0; i--) {
		    int j = rand.nextInt(i + 1);

		    String temp = deck[i];
		    deck[i] = deck[j];
		    deck[j] = temp;
	    }
    }

	//взять одну карту
    public static String give_first_card() {
		return deck[card_now++];
    }

	public  static String[] see_all_cards() {
		return deck;
	}
}

class Player {
	public int kol_vo_card = 0;
	public int summ_card = 0;
	int clear_summ = 0;
	int tus_col = 0;
	public String[] cards = new String[11];

	//берет карту из деки
	//возврат стистики:
	//0 - проигрыш, 1 - продолжаем, 2 - победа, а массив карт хранится в игроке
	public String take_card(Deck deck) {
		String card = deck.give_first_card();
		cards[kol_vo_card++] = card;

		// Получаем ранг карты (первое слово)
		String rank = card.split(" ")[0];

		switch (rank) {
			case "Туз":
				tus_col++;
				break;
			case "Валет", "Дама", "Король":
				clear_summ += 10;
				break;
			default:
				clear_summ +=Integer.parseInt(rank);
				break;
		}
		if (tus_col == 0){
			summ_card = clear_summ;
		}
		else {
			if (summ_card + tus_col * 11 > 21){
				summ_card = tus_col + clear_summ;
			}
			else {
				summ_card = tus_col * 11 + clear_summ;
			}
		}
		return card;
	}

	public void show_my_cards() {
		if  (kol_vo_card == 0) {
			System.out.print("[]");
			return;
		}

		System.out.print("[");
		for (int i = 0; i < kol_vo_card-1; i++) {
			System.out.print(cards[i] + ", ");
		}
		System.out.print(cards[kol_vo_card - 1]);
		System.out.print("]");
		System.out.println(" => " + summ_card);
	}
}

class Krupie extends Player {
	public Integer diller_turn(Deck deck, Krupie krupie, Player player) {
		while (krupie.summ_card < 17 || krupie.summ_card < player.summ_card) {
			System.out.printf("\tКрупье получает карту - %s, сумма - %s\n", krupie.take_card(deck), krupie.summ_card);
		}
		if (krupie.summ_card > 21){
			return 0;
		}
		if (krupie.summ_card == 21 || krupie.summ_card > player.summ_card) {
			return 1;
		}
		return 0;
	}
}

public class blackjack_main {
	static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
		System.out.println("Добро пожаловать в Блэкджек!");
		int num_raund = 0;
		int pl_vin = 0;
	    int kaz_vin = 0;

	    String answer = "Yes";

		while (Objects.equals(answer, "Yes")) {
			num_raund++;
			switch (raund(num_raund)){
				case 1:
					pl_vin++;
					System.out.printf("Вы выиграли раунд! Счет %d:%d\n", pl_vin, kaz_vin);
					break;
				case 0:
					kaz_vin++;
					System.out.printf("Вы проиграли раунд( Счет %d:%d\n", pl_vin, kaz_vin);
					break;
			}
			System.out.println("Продолжаем?");
			answer = scanner.nextLine();
		}
    }


	//1 - победа игрока
	//0 - победа крупье
	static int raund(int n){
		//инит всего
		Player player = new Player();
		Krupie krupie = new Krupie();
		Deck deck = new Deck();
		deck.up_date_deck();

		//начало игры

		System.out.println("Раунд " + n);
		System.out.println("Дилер раздал карты");

		player.take_card(deck);
		player.take_card(deck);
		System.out.print("\tВаши карты:");
		player.show_my_cards();

		if (player.summ_card == 21){
			return 1;
		}
		if (player.summ_card > 21){
			return 0;
		}

		krupie.take_card(deck);
		System.out.print("\tКарты дилера:");
		krupie.show_my_cards();


		System.out.println("Ваш ход:");
		while (true){
			System.out.println("\t Take - взять карту, Pass - пасс");
			String answer = scanner.nextLine();
			switch (answer){
				case "Take":
					System.out.printf("\tВы получили - %s\n", player.take_card(deck));
					System.out.print("\tВаши карты:");
					player.show_my_cards();
					System.out.print("\tКарты дилера:");
					krupie.show_my_cards();

					if (player.summ_card == 21){
						return 1;
					}
					if (player.summ_card > 21){
						return 0;
					}
					break;

				case "Pass":
					System.out.println("\nХод диллера");
					return switch (krupie.diller_turn(deck, krupie, player)) {
						case 1 -> 0;
						case 0 -> 1;
						default -> 0;
					};

				default:
					break;
			}


		}
	}
}



