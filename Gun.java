
/**
 * Write a description of class Gun here.
 *
 * DROGOBO
 */

// We are not allowed to import things that might make this easier for is like the random class due to AP restrictions
import java.util.Arrays;

public class Gun
{
    private boolean[] shells; // This has all the shells in the gun
    // True means that it is live
    // False means that it is blank

    private int rounds;
    public int getRounds() {return shells.length;} // Oneline getter
    private int liveRounds;
    private int blankRounds;
    private int[] dealerKnownRounds = new int[0]; // When the dealer uses a telephone, add the known shells to this
    private boolean cut; // This is for the knife that lets you deal double damage
    public boolean getCut() {return cut;} // Oneline getter

    // Be able to add to the known rounds (for the dealer)
    public void addToKnownRounds(int round) {
        int[] newKnownRounds = new int[dealerKnownRounds.length + 1]; // Make it one entry longer than currently known shells
        // Add everying from the previous array to the new array
        for (int i = 0; i < dealerKnownRounds.length; i++) {
            dealerKnownRounds[i] = dealerKnownRounds[i];
        }
        newKnownRounds[dealerKnownRounds.length] = round; // Add the item as the last thing in the array
        dealerKnownRounds = newKnownRounds; // Finally assign it back to this
    }
    
    // Let the dealer use the current shell logic for phones
    public boolean isCurrentShellKnown() {
        if (dealerKnownRounds.length != 0) { // Make sure the known rounds list has something
            // Loop through all to see if there is a zero
            for (int i : dealerKnownRounds) {
                if (i == 0) {
                    // A zero means that the current shell is known
                    return true;
                }
            }
            // Return false if it isn't
            return false;
        } else {
            return false;
        }
    }
    
    // Also be able to remove a round from the known round list
    public void removeKnownRound() {
        int badNumbers = 0; // Accumulate the number of -1 to remove
        // Find the amount of already used shells to remove
        for (int i : dealerKnownRounds) {
            if (i == -1) {
                badNumbers++;
            }
        }
        
        int[] newKnownRounds = new int[dealerKnownRounds.length - badNumbers]; // Make it shorter to remove -1
        int offset = 0; // Offsetting the array to skip numbers is also important
        for (int i = 0; i < dealerKnownRounds.length; i++) { // Loop through and skip -1
            if (dealerKnownRounds[i] == -1) { // Increase offset to skip -1
                offset++;
            }
            newKnownRounds[i] = dealerKnownRounds[i + offset]; // Now it is assigned
        }
        
        dealerKnownRounds = newKnownRounds; // Overwrite to finalize
    }
    
    // Subtract all of them by one so it works well enough
    public void moveKnownRounds() {
        // Using a foreach loop to loop over all elements
        removeKnownRound(); // Get rid of all superfluous -1 from the array
        for (int i : dealerKnownRounds) {
            i--; // Subtract each by one
        }
    }
    
    public Gun()
    {
        // It does nothing here because the game manager does this instead
        // This makes the same gun object reusable multiple times
    }

    public void init() { // Basically we want to make the gun actually usable
        rounds = Randomness.getRandomInt(2,9); // Generate a random amount of shells to load from 2 to 8
        liveRounds = (int)(((Randomness.getRandomInt(3,6)/8.0)*rounds)); // This chooses how many shells are allowed to be live and blank
        // If there are only two, force one of the two to be live
        if (rounds == 2) {
            liveRounds = 1;
        }
        // If a shell is not live, then it is blank
        blankRounds = rounds - liveRounds;

        System.out.println("\n" + liveRounds + " LIVE. " + blankRounds + " BLANK."); // Tell the user about the gun status

        shells = new boolean[rounds]; // Make the array have the amount of rounds
        loadGun(); // Use this method to actually insert the shells randomly
        dealerKnownRounds = new int[0]; // Dealer has no idea what the shells are from a phone
    }

    // This will return the current shell in the chamber
    // This is for the magnifying glass
    public boolean peek() {
        return shells[0];
    }

    // This one is for the phone

    public boolean find(int round) {
        return shells[round];
    }

    // This method is for the inverter item
    // It will invert the current shell
    public void invert() {
        if (shells[0]) {
            shells[0] = false;
        } else {
            shells[0] = true;
        }
        System.out.println("Inverted current shell.");
    }

    // This will either shoot a live or blank round
    // Live rounds do 1 damage, blanks do 0 damage, and cut live rounds do 2 damage
    public int pullTrigger() {
        // It first gets the status of the current shell
        boolean shell = shells[0];
        int damage = 0; // We will add to this later if it is a live

        if (shell) {
            System.out.println("LIVE.");
            damage++; // Add one to the damage that it is going to deal
        } else {
            System.out.println("BLANK.");
        }

        // Deal double damage if the gun is cut 
        if (cut) {
            damage *= 2; // Multiply by two if it is cut in half
            cut = false;
            System.out.println("Gun goes back to dealing the normal amount of damage.");
        }

        // Then it has to eject the shell and return it as a result
        ejectCurrentShell();

        return damage;
    }

    // This helps so much with reusability
    // Instead of having to rewrite this a lot, I wrote it once
    public boolean shootPlayer(Player target, Player self) {
        // Print the play-by-play
        System.out.println("\n" + self.getName() + " is shooting " + target.getName() + ".");
        boolean keepGoing = true; // Assume it's true to start
        int damage = pullTrigger(); // Determine whether to do damage or not
        target.decreaseHealth(damage); // Deal damage to the target
        System.out.println(target.getName() + " has " + target.getHealth() + " charges."); // This is important to show the step-by-step play
        if (damage > 0) {
            keepGoing = false; // End the player's turn if they shoot themself and hit
        }
        return keepGoing; // This will return true if the shell is a blank
    }

    public void loadGun() {
        // Start by adding the number of live rounds to the gun and making all the rest blanks
        for (int i = 0; i < rounds - 1; i++) {
            if (i <= (liveRounds - 1)) { // You have to subtract one to ensure that it is consistent with the loop
                shells[i] = true;
            } else {
                shells[i] = false;
            }
        }

        // Then we have to mix them up
        scramble();
    }

    private void scramble() {
        // This method is supposed to scramble up the shells in the gun so that you don't know what it is
        // Basically, it will choose two different shells in the chamber and then swap their position a few times
        // The number of times it swaps will be 16(rounds) + 1 or 2
        // This is to prevent cases of only two shells from being trivial if you know the code

        int randomExtraTimes = Randomness.getRandomInt(1, 3);

        // Now we enter the loop
        for (int i = 0; i < rounds * 16 + randomExtraTimes; i++) {
            // Start by picking the first shell which we want to swap
            int firstShell = Randomness.getRandomInt(0, rounds);
            // Then we pick another at random
            int secondShell = Randomness.getRandomInt(0, rounds);
            // If these happen to be the same, then we use a while loop to change one of them
            while (secondShell == firstShell) {
                secondShell = Randomness.getRandomInt(0, rounds); // Reassign it so that we aren't in an infinite loop
            }

            // Then we make a swap based on the array
            // Step 1
            boolean buffer = shells[firstShell]; // This var temporarily stores the first chosen shell's state so that we can reassign it later
            // Step 2
            shells[firstShell] = shells[secondShell]; // Change the first one to be the same as the second
            // Step 3
            shells[secondShell] = buffer; // Finally change the second shell to the original value of the first
        }
    }

    public boolean ejectCurrentShell() {
        // Does exactly what it sets out to do
        // The plan is to make an array that has one element less than the original array because of the element that we removed and then set the original array to the new array
        // It sounds complicated, but just watch
        // I also want to return the shell that was ejected
        boolean toReturn = shells[0];
        boolean[] newShells = new boolean[shells.length - 1]; // Declare an array that is 1 smaller than the original array
        // Now we have to loop through and add all but the first shell to newShells
        for (int i = 1; i < shells.length; i++) { // This skips the first shell
            newShells[i - 1] = shells[i]; // Now it is assigned
        }
        // To finish it off, we have to reassign the old array to the new one
        shells = newShells;

        // I also want to update the dealer's knowledge of the gun
        moveKnownRounds();
        
        // Don't forget about the shell!
        return toReturn;
    }

    // Small helper method to determine if the gun is empty or not
    public boolean isEmpty() {
        return shells.length <= 0; // No rounds certainly means the gun is empty
    }

    // Here is a method to let the dealer cheat
    public boolean returnLastShell() {
        return shells[shells.length - 1];
    }

    // This is for when you cut the gun
    public void cut() {
        cut = true;
        System.out.println("Gun will deal double damage on the next shot!");
    }
}
