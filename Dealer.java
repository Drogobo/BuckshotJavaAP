
/**
 * Write a description of class Dealer here.
 *
 * DROGOBO
 */
public class Dealer extends Player // This class extends Player because it has a few things added onto it that the base class, player, does not have
// This means that it has everything the base class has and more
{
    // The dealer sort of has an unfair advantage
    // He can tell what the last round is every time garunteed
    // But he is really stupid and bold

    private boolean knowsCurrentShell = false; // They should have to know the current shell before making a decision
    private boolean currentShell; // If he uses GLASS, then he will know what to do with the current shell
    private String wantsToSteal; // For using ADRENALINE

    // Setting variables for the current shell from outside
    public void setKnowsCurrentShell(boolean x) {knowsCurrentShell = x;}

    public void setCurrentShell(boolean x) {currentShell = x;}

    // Helping the ADRENALINE method
    public String getWantsToSteal() {return wantsToSteal;}

    // Anways, here's a default constructor

    public Dealer() {
        setName("DEALER");
    }

    // This is the main logic for the dealer
    @Override // Override the child method in the player class
    public void playTurn(Gun gun, Player player) {
        if (!isHandcuffed()) {
            boolean keepGoing = true; // Sentinel value to control loop
            
            // Loop for his turn
            while (!(gun.isEmpty()) && keepGoing) {
                // Check to see if the dealer knows the current shell
                if (gun.isCurrentShellKnown()) {
                    // Then update variables
                    knowsCurrentShell = true;
                    currentShell = gun.peek();
                }
                // Use as many items as possible first
                // Goes in a (sort of) random order of items to use
                boolean usingItems = true;
                // Start looping to use items
                while (usingItems && (getItems().length > 0 && gun.getRounds() > 1)) {
                    // The following if-else ladder is the logic of the dealer
                    // It may not be accurate to the game. IDK
                    // Smoke indefinitely until he uses all the CIGARETTES or until he heals too much
                    while (getHealth() < getMaxHealth() && lookForItem("CIGARETTE", gun, player)) {/* Do nothing lol */}
                    
                    // Now try to use a glass
                    if (gun.getRounds() > 1 && lookForItem("GLASS", gun, player)) { // If we can use GLASS
                        knowsCurrentShell = true;
                        if (!currentShell && lookForItem("INVERTER", gun, player)) {
                            currentShell = true;
                        }
                        usingItems = false; // Prevent him from using any more items
                    } else {
                        // If there is no glass to use, then move here and try using other items
                        int item = -1;
                        item = Randomness.getRandomInt(0, getItems().length);
                        // This part of the method will only allow these items to be used
                        if (has("BEER") == -1 && has("PHONE") == -1 && (has("HANDCUFFS") == -1
                            || player.isHandcuffed()) && (has("MEDICINE") == -1 || !(getHealth() < getMaxHealth() - 1))) {
                            usingItems = false; // Then he should stop using all items
                        } else {
                            // Allow the use of these items:
                            if (getItemAt(item).equals("BEER") ||
                            getItemAt(item).equals("PHONE") ||
                            (getItemAt(item).equals("HANDCUFFS") && !(player.isHandcuffed()))) {
                                Items.useItem(item, gun, this, player); // One of these items
                            } else if (getItemAt(item).equals("MEDICINE") && getHealth() < getMaxHealth() - 1) {
                                // And also MEDICINE if they need 2 charges to get to the max
                                Items.useItem(item, gun, this, player);
                            }
                        }
                    }
                }
                
                // First make sure he knows if he knows the shell
                if (gun.getRounds() == 1) {
                    // He can't cheat if he doesn't know the amount of rounds in the gun
                    // By cheating I mean knowing the final round every single time
                    knowsCurrentShell = true;
                    currentShell = gun.returnLastShell(); // The gun whispers the polarity of the last shell into his ear
                    // If he cheats and finds a live, then shoot the player
                    if (currentShell) {
                        lookForItem("KNIFE", gun, player); // Try to use a knife
                        gun.shootPlayer(player, this);
                    } else { // Otherwise, shoot himself
                        // Try to invert to deal damage to the player
                        if (lookForItem("INVERTER", gun, player)) {
                            gun.shootPlayer(player, this);
                        } else {
                            // This means to shoot himself
                            gun.shootPlayer(this, this);
                        }
                    }
                    keepGoing = false; // His turns ends here                    
                } else { // If this is not the last round
                    // If he doesn't know the current shell
                    if (!knowsCurrentShell) {
                        // This is essentially a coin flip
                        int randomBinary = (int)(Math.random() * 2);
                        boolean coinFlip = (1 == randomBinary);
                        if (coinFlip) {
                            // Shoot the player
                            gun.shootPlayer(player, this);
                            keepGoing = false;
                        } else {
                            // Shoot himself
                            keepGoing = gun.shootPlayer(this, this);
                        }
                    } else {
                        // Account for situations that could occur if he knows the current round
                        
                        // Switch the polarity if he can get his hands on an inverter
                        if (!currentShell && lookForItem("INVERTER", gun, player)) {
                            currentShell = true;
                        }
                        
                        // Then follow this pattern for other items
                        if (currentShell) {
                            // Try to use a knife and then shoot the player
                            lookForItem("KNIFE", gun, player);
                            gun.shootPlayer(player, this);
                            keepGoing = false;
                        } else {
                            // Shoot himself and continue his turn
                            gun.shootPlayer(this, this);
                            keepGoing = true;
                        }
                    }
                }
                // He does not know the next shell
                knowsCurrentShell = false;
                // Even if he does, we will find that out when the loop reruns
            }
        } else {
            // Account for being handcuffed
            System.out.println(getName() + " can't play their turn because they are handcuffed.");
        }
    }

    // This lets the dealer find an item if the other player has it
    public boolean lookForItem(String item, Gun gun, Player player) {
        boolean success = true; // If it found something and used it, it will say it worked
        if (has(item) != -1) {
            Items.useItem(has(item), gun, this, player); // Use the item directly from our inventory if we have it
        } else if (player.has(item) != -1 && has("ADRENALINE") != -1) {
            // Use adrenaline to steal the player's item
            wantsToSteal = item; // Let the Items class find this later
            Items.useItem(has("ADRENALINE"), gun, this, player);
        } else {
            success = false; // We did not get what we wanted
        }
        return success; // Tell the dealer if the item was found or not
    }
}
