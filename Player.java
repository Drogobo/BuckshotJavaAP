
/**
 * Write a description of class Player here.
 *
 * DROGOBO
 */

// This class stores some information about the player

public class Player
{
    // Quick instance variables to make the game easier for us
    private int health;
    private String name;
    private String[] items = new String[0]; // They have 0 items to start
    private boolean handcuffed; // The player could be handcuffed
    // Since the dealer extends the player, I will add his lastRound thing here
    // I know I could technically do this in some other place, but this works
    private boolean lastRound; // He will find out at the start of each round what the last round of the gun is
    // He can't really use that knowledge though
    private int maxHealth; // Only allow the player to heal a certain amount

    public Player() { 
        // Nothing to do here
        health = 0;
        name = "DEALER";
        handcuffed = false;
    }

    // Add items to the player's inventory
    // This is also the same for adrenaline
    public void addItem(String item) {
        String[] moreItems = new String[items.length + 1]; // Make it one entry longer than current items
        // Make sure the player has less than 8 items before adding to their total
        if (items.length < 8) {
            // Add everying from the previous array to the new array
            for (int i = 0; i < items.length; i++) {
                moreItems[i] = items[i];
            }
            moreItems[items.length] = item; // Add the item as the last thing in the array
            items = moreItems; // Finally assign it back to this
        }
    }

    // Remove an item from a player's inventory
    // After they use it or after they steal it
    public void removeItem(int item) {
        // Precondition or else we will get an error
        if (items.length > 0) {
            String[] lessItems = new String[items.length - 1]; // 1 entry shorter than current array
            // Make sure the player has at least 1 item before starting

            // Add everying from the previous array to the new array EXCEPT FOR THE REMOVED ITEM
            int offset = 0; // This value allows us to skip an array item
            for (int i = 0; i < lessItems.length; i++) {
                if (i == item) {
                    offset++; // Offset the counter to skip over the one item
                }
                lessItems[i] = items[i + offset]; // THIS IS WHERE ALL THE MONEY IS AT!!!!
            }
            // Finally, reassign it
            items = lessItems;
        }
    }

    // Method to let the player use the gun by themself without the game controlling it
    public void playTurn(Gun gun, Player dealer) {
        boolean keepGoing = true; // Sentinel value to control loop
        if (!handcuffed) { // Make sure the player is not cuffed before giving them a turn
            while (keepGoing && !(gun.isEmpty())) {
                System.out.println("\n1. Pick up gun\n2. Use item");
                // Now we have to get their input
                String choice = ""; // Blank string to represent a sort of sentinel value
                while (!(choice.equals("1") || choice.equals("2"))) { // .equals() to compare strings!!!!!!!
                    choice = UserInput.getString("Select an action"); choice.trim(); // Use this to fix up the string
                }
                if (choice.equals("1")) {
                    choice = ""; // Blank string to represent a sort of sentinel value
                    System.out.println("\n1. Shoot yourself" + 
                        "\n2. Shoot " + dealer.getName());
                    while (!(choice.equals("1") || choice.equals("2"))) {
                        choice = UserInput.getString("Select an action"); choice.trim(); // Use this to fix up the string
                    }
                    if (choice.equals("1")) {
                        // Shoot the player
                        keepGoing = gun.shootPlayer(this, this); // The bool that shootPlayer() returns is just for this purpose
                    } else if (choice.equals("2")) {
                        // Shoot the dealer
                        gun.shootPlayer(dealer, this); // Shoot the dealer
                        keepGoing = false; // End the player's turn
                    }
                } else {
                    Items.useItem(UserInput.getItem(this), gun, this, dealer); // It will get an item to use
                }
            }
        } else {
            System.out.println(getName() + " can't play their turn because they are handcuffed.");
            uncuff();
        }
    }

    // Another constructor couldn't hurt
    public Player(String name) {
        setName(name); // Sending it over to the other method to save me time
    }

    // Method to control handcuffing
    public boolean handcuff() {
        // Very easy if-else ladder to invert the state of cuffs
        if (!handcuffed) {
            handcuffed = true;
            System.out.println(name + " is handcuffed.");
            return handcuffed;
        }
        return false;
    }

    // Uncuff if they are cuffed
    public void uncuff() {
        handcuffed = false;
    }

    // These methods are so self-explanatory
    public String getName() {return name;}

    public int getHealth() {return health;}
    public int getMaxHealth() {return maxHealth;}

    public boolean isHandcuffed() {return handcuffed;}

    // toUpperCase() because the game makes all names in uppercase
    public void setName(String n) {name = n.toUpperCase();}

    public void setHealth(int h) {health = h; maxHealth = h;}

    public void decreaseHealth(int h) {health -= h; System.out.println(name + " lost " + h + " charges.");}

    // This method is more complicated
    public void increaseHealth(int h) {
        // If they don't go over the health limit
        if (health + h <= maxHealth) {
            health += h;
            System.out.println(name + " gained " + h + " charges.");
        } else {
            // Set the health to the max health because it can't go over
            health = maxHealth;
            System.out.println("You can't heal over " + maxHealth + ", so now you have " + health + " charges instead.");
        }
    }

    // Overriding this method of base Object
    public String toString() {
        // Print import parts of the player
        String information = "";
        information += getName() + " has " + getHealth() + " charges and these items:"; // Add their name to the information
        // Also make sure every item gets added
        for (String i : items) { // Foreach loop adds them to the string one by one
            information += "\n - " + i;
        }
        return information;
    }

    // Return all of the items that the player has
    public String[] getItems() {
        return items;
    }

    public void cheat(boolean finalRound) {
        // This method lets the dealer know ahead of time what the last round is
        lastRound = finalRound;
    }
    
    // Determine if the dealer has a specific item and return the position in the array
    public int has(String item) {
        for (int i = 0; i < getItems().length; i++) {
            if (getItems()[i].equals(item)) {
                return i; // This means he has at least one of the items
            }
        }
        return -1;
    }
    
    // Find and return the item at i
    public String getItemAt(int i) {
        return items[i];
    }
}
