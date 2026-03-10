package by.magofrays;

import by.magofrays.tree.DSLTree;

import java.util.List;
import java.util.Scanner;

public class Main {

    private final UserInputParser userInputParser = new UserInputParser();
    public static void main(String[] args) {
        Main main = new Main();
        System.out.println("Если ввод пустой, программа завершит свою работу.");
        while(true){
            List<String> words = main.userInputParser.parse(System.in);

            if (words.isEmpty()) {
                System.out.println("Ввод завершен. Хотите продолжить? (да/нет)");
                Scanner scanner = new Scanner(System.in);
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("нет") || answer.equalsIgnoreCase("н")) {
                    break;
                }
                continue;
            }
            try{
                var result = new DSLTree(words, 0);
                System.out.println(result.getNode());
            }catch(IllegalArgumentException e){
                System.out.println(e.getMessage());
            }


        }
    }
}
