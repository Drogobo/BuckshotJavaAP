
/**
 * Write a description of class UserInput here.
 *
 * DROGOBO
 */

// I am using this to make user input more consistent across classes
import java.util.Scanner;

// Everything in this class is static because it stays the same

public class UserInput
{
    // You can actually just assign an instance variable here
    private static Scanner scanny = new Scanner(System.in);

    // This is the method that I will use most to take in user input
    public static String getString(String wanted) {
        // This prompts the user to enter information
        System.out.print("" + wanted + ": ");
        return scanny.nextLine(); // This line gets and sends the user's input
    }

    public static int getInt(String wanted) {
        // This prompts the user to enter information
        System.out.print("" + wanted + ": ");
        return scanny.nextInt(); // This line gets and sends the user's input
    }

    // This one is to get the integer for items instead of string
    // I realized this was possibly a better system
    public static int getItem(Player player) {
        if (player.getItems().length > 0) { // Player must have at least 1 item to use
            // This asks the user to choose one of their items
            // Loop over the array of items to print
            for (int i = 0; i < player.getItems().length; i++) {
                System.out.println((i+1) + ". " + player.getItems()[i]); // For example: 1. BEER
            }

            int choice = -2; // Sentinel value
            while (!(choice > 0 && choice < player.getItems().length + 1)) {
                choice = getInt("Choose an item"); // No knuckleheadery
            }
            // Now just return the item it corresponds with
            // ALSO CONSUME THE ENTER KEY
            scanny.nextLine();
            return choice - 1;
        }
        return -1; // If they do not have an item to use
    }
}
