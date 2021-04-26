import bank.Bank;

import java.util.Scanner;

public class Main extends Application {

    private static Scanner s = new Scanner(System.in);

    // Nettoie l'écran des prints précédents
    private static void flushScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {

        // Init
        Bank b = new Bank();

        /// Declaration before loop
        boolean endOfSession = false;
        String userInput;

        int balance;
        int threshold;
        String name;

        // Loop
        while (!endOfSession) {

            // Menu display
            System.out.println("\n\nWhat operation do you want to do ?");
            System.out.println("0. See all accounts");
            System.out.println("1. Create a new account");
            System.out.println("2. Change balance on a given account");
            System.out.println("3. Block an account");
            System.out.println("q. Quit\n");

            // Getting primary input
            userInput = s.nextLine();

            // Processing user input
            switch (userInput) {
                case "q":
                    endOfSession = true;
                    b.closeDb();
                    break;
                case "0":
                    b.printAllAccounts();
                    break;
                case "1":
                    System.out.println("\nEnter a name for the account :");
                    name = s.nextLine();
                    System.out.println("\nEnter the balance of the account : ");
                    balance = s.nextInt();
                    System.out.println("\nEnter the threshold of the account : ");
                    threshold = s.nextInt();
                    b.createNewAccount(name,balance,threshold);
                    flushScreen();
                    break;
                case "2":
                    System.out.println("\nEnter the name of the account that you want to modify : ");
                    name = s.nextLine();
                    System.out.println("\nEnter the new balance : ");
                    balance = s.nextInt();
                    b.changeBalanceByName(name,balance);
                    flushScreen();
                    break;
                case "3":
                    System.out.println("\nEnter the name of the account you want to lock : ");
                    name = s.nextLine();
                    b.blockAccount(name);
                    flushScreen();
                    break;
            }
        }

    }
}

