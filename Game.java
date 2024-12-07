
/**
 * Write a description of class Game here.
 *
 * DROGOBO
 */

// This class does the main game loop

public class Game
{
    private static int level; // This is which level of the game we are at
    private static int winnings; // Amount of money you win
    private static boolean dealersTurn; // Every time we check this, we have to remember that we are talking about the dealer's turn and not the players turn
    public static void main(String[] args) {
        // First we can get their name        
        String name = ""; // Sentinel value to make the loop run guarunteed
        // Error check for names that are banned
        while ((name.equals("DEALER") || name.equals("GOD")) || name.equals("")) {
            // Making the names uppercase before comparing makes the comparison much easier to do
            name = UserInput.getString("Sign the contract"); // This UserInput class makes everything squeaky clean
            name = name.trim(); name = name.toUpperCase(); // This is the standard for buckshot naming
        }

        Gun gun = new Gun(); // Start out with making a new gun object to use for the rest of the game
        Player player = new Player(name); // Make a player
        Dealer dealer = new Dealer(); // The dealer class extends the player class
        // Hint: if you want local multiplayer, you can change the dealer class to player
        // It works the way you want it to

        // Both players will start with the same amount of health
        int health = Randomness.getRandomInt(2,6);
        player.setHealth(health); dealer.setHealth(health);
        
        // Enter the main game loop
        do {
            gun.init(); // Make the gun usable for the round
            dealer.cheat(gun.returnLastShell()); // Give the dealer insight into the last round
            // Both players also get the items
            int items = Randomness.getRandomInt(2,6);
            for (int i = 0; i < items; i++) {
                player.addItem(Items.getRandomItem());
                dealer.addItem(Items.getRandomItem());
            }
            
            // Print the player information to the console
            System.out.println(player);
            System.out.println(dealer);

            // Make it the player's turn first
            dealersTurn = false;

            // Now we get to the FUN!
            while (!(gun.isEmpty()) && (player.getHealth() > 0 && dealer.getHealth() > 0)) { // A new round will begin when the gun is completely empty
                // Now we make a crossroads
                // The player or dealer will have a turn
                if (!dealersTurn) { // Not the dealer's turn means it is the player's turn
                    System.out.println("\n" + player.getName() + " is up."); // Newline for easier reading
                    player.playTurn(gun, dealer);
                    dealersTurn = true;
                } else { // Now we enter the code for the dealer
                    System.out.println("\n" + dealer.getName() + " is up."); // Newline for easier reading
                    dealer.playTurn(gun, player);
                    dealersTurn = false;
                }
                
            }

        } while (player.getHealth() > 0 && dealer.getHealth() > 0); // This do while condition makes sure the game reruns if nobody loses for another round
        
        // Now we can print results
        if (player.getHealth() > dealer.getHealth()) {
            System.out.println(player.getName() + " WINS!");
        } else {
            System.out.println(dealer.getName() + " WINS!");
        }
    }
}
