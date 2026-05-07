package ru.nsu.tumilevich;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide config file as command line argument");
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            PizzeriaConfig config = mapper.readValue(new File(args[0]), PizzeriaConfig.class);
            PizzeriaEngine engine = new PizzeriaEngine(config);

            int maxId = engine.start();
            int orderCounter = maxId + 1;

            System.out.println("Pizzeria is open! Type 'order' to add order, 'exit' to close.");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) {
                    engine.stop();
                    break;
                } else if ("order".equalsIgnoreCase(input)) {
                    engine.addNewOrder(orderCounter++);
                }
            }

            System.out.println("Pizzeria closed.");
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            System.out.println("Please check config.json path or create it.");
        }
    }
}