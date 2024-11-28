import automata.FiniteAutomata;
import parser.Grammar;
import parser.LR0Parser;
import scanner.TokenScanner;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        printMenuGrammar();
        Grammar grammar = new Grammar("gt.txt");

        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine().trim();

        while (!option.equals("0")) {
            switch (option) {
                case "1":
                    System.out.println("Nonterminals: " + grammar.getNonTerminals());
                    break;

                case "2":
                    System.out.println("Terminals: " + grammar.getTerminals());
                    break;

                case "3":
                    System.out.println("Starting symbol: " + grammar.getStartingSymbol());
                    break;

                case "4":
                    System.out.println("Productions: " + grammar.productionsToString());
                    break;

                case "5":
                    System.out.print("Write the nonterminal: ");
                    Scanner scanner2 = new Scanner(System.in);
                    String sequence = scanner2.nextLine();

                    System.out.println(grammar.productionsOfNonterminalToString(sequence.trim()));
                    break;

                case "6":
                    System.out.println("The grammar is context free: " + grammar.isContextFree());
                    break;

                case "7":
                    LR0Parser parser = new LR0Parser(grammar);

                    System.out.println(parser.getCanonicalCollection().toString());
                    break;

                case "?", "h":
                    printMenuGrammar();
                    break;

                default:
                    System.out.println("Invalid command!");
                    break;

            }

            System.out.print("\n> ");
            option = scanner.nextLine().trim();
        }
    }

    public static void mainFA(String[] args) throws Exception{
        TokenScanner tscanner = new TokenScanner();
        tscanner.scan("p1err.txt");

        FiniteAutomata fa = new FiniteAutomata("fa.txt");

        Scanner scanner = new Scanner(System.in);
        printMenuFA();
        System.out.print("> ");
        String option = scanner.nextLine().trim();

        while (!option.equals("0")) {
            switch (option) {
                case "1":
                    System.out.println("All states: " + fa.getStates());
                    break;

                case "2":
                    System.out.println("Alphabet: " + fa.getAlphabet());
                    break;

                case "3":
                    System.out.println("Initial state: " + fa.getInitialState());
                    break;

                case "4":
                    System.out.println("Final states: " + fa.getFinalStates());
                    break;

                case "5":
                    System.out.println("Transitions:\n" + fa.transitionsToString());
                    break;

                case "6": {
                    System.out.println("Write a sequence: ");
                    Scanner scanner2 = new Scanner(System.in);
                    String sequence = scanner2.nextLine();

                    if (fa.acceptsSequence(sequence))
                        System.out.println("Sequence is valid");
                    else
                        System.out.println("Invalid sequence");
                }
                break;

                default:
                    System.out.println("Invalid command!");
                    break;

            }

            System.out.print("\n> ");
            option = scanner.nextLine().trim();
        }
    }

    private static void printMenuGrammar() {
        System.out.println("1. Print all the nonterminals");
        System.out.println("2. Print all the terminals");
        System.out.println("3. Print the starting symbol");
        System.out.println("4. Print all the productions");
        System.out.println("5. Print all the productions for a given nonterminal");
        System.out.println("6. Check if the grammar is context-free");
        System.out.println("7. Print the canonical collection of the grammar");
        System.out.println("0. Exit | ?,h - print the menu");
    }

    private static void printMenuFA() {
        System.out.println("1. Print all the states");
        System.out.println("2. Print the alphabet");
        System.out.println("3. Print the initial state");
        System.out.println("4. Print the final states");
        System.out.println("5. Print all the transitions");
        System.out.println("6. Check if a sequence is accepted by the DFA");
    }
}
