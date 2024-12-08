
/**
 * Write a description of class Items here.
 *
 * DROGOBO
 */
public class Items
{
    // The goal of this is to manage item usage
    // This is also a constant because nothing is allowed to change this ever
    // Here are a bunch of items because I would rather die than rewrite item strings multiple times
    private static final String BEER = "BEER", // Eject the current shell
    ADRENALINE = "ADRENALINE", // Steal an item
    CIGARETTE = "CIGARETTE", // Heal up by 1
    PHONE = "PHONE", // Know the polarity of a random shell
    GLASS = "GLASS", // See the current shell
    MEDICINE = "MEDICINE", // Heal up by 2 or lose 1 charge
    INVERTER = "INVERTER", // Invert the current shell's polarity
    HANDCUFFS = "HANDCUFFS", // Skip the other player's turn
    KNIFE = "KNIFE"; // Make the gun do double damage this time

    // Now I gotta add these to the array of items
    private static final String[] ITEMS = {
            // Start adding items here
            // 9 in total
            BEER,
            ADRENALINE,
            CIGARETTE,
            PHONE,
            GLASS,
            MEDICINE,
            INVERTER,
            HANDCUFFS,
            KNIFE,
        };

    public static String[] getItemList() {
        return ITEMS;
    }

    // Get random items from the list
    public static String getRandomItem() {
        return ITEMS[Randomness.getRandomInt(0, ITEMS.length)];
    }

    // This method is purely to match items to the functions
    public static void useItem(int item, Gun gun, Player user, Player opponent) {
        if (item != -1) { // Make sure the user is using a valid item before starting
            String toUse = user.getItems()[item]; // Get the string for the item which the player will use
            user.removeItem(item); // Remove it from their inventory;

            // Inform the users of the items used
            System.out.println("\n" + user.getName() + " used " + toUse + ".");

            // Heads up: dealer has special rules he plays by
            switch(toUse) { // Compare to cases that it could be
                    // I want to avoid writing the items more times than I have to
                    // Time to use the items!
                case BEER:
                    beer(gun);
                    break;
                case ADRENALINE:
                    stealFrom(gun, user, opponent);
                    break;
                case CIGARETTE:
                    user.increaseHealth(1); // Add 1 to their health
                    break;
                case PHONE:
                    usePhone(gun, user);
                    break;
                case GLASS:
                    boolean shell = peek(gun, user);
                    // Check to see if the player is a dealer
                    // This is what instanceof does
                    // It returns a boolean to see if a certain Object is a subclass of another object
                    if (user instanceof Dealer) {
                        setDealerKnowledge((Dealer)user, shell); // Cast the Player object to a Dealer because that is really what it is
                    }
                    break;
                case MEDICINE:
                    takeMedicine(user);
                    break;
                case INVERTER:
                    gun.invert();
                    break;
                case HANDCUFFS:
                    if (!opponent.handcuff()) {
                        System.out.println("Opponent is already cuffed");
                        user.addItem(HANDCUFFS);
                    }
                    break;
                case KNIFE:
                    gun.cut();
                    break;
            }
        }
    }

    private static void usePhone(Gun gun, Player user) {
        if (!(user instanceof Dealer)) {
            // Player's code
            if (gun.getRounds() > 1) {
                int randomShell = -1; // Make sure the while loop runs
                // Do not allow it to reveal the current shell
                while (randomShell < 0 || randomShell == gun.getRounds()) {
                    randomShell = Randomness.getRandomInt(1, gun.getRounds() - 1); // This is a RANDOM shell
                }
                int shell = randomShell + 1; // This is the way we are going to talk about the next shell
                if (gun.find(randomShell)) { // If it is live
                    System.out.println("SHELL #" + shell + "... LIVE ROUND...");
                } else { // If it is blank
                    System.out.println("SHELL #" + shell + "... BLANK ROUND...");
                }
            } else {
                System.out.println("GOOD LUCK... "); // They can't use it when there is only one shell in the gun
            }
        } else {
            // Dealer's code
            if (gun.getRounds() > 1) {
                int randomShell = -1; // Make sure the while loop runs
                // Do not allow it to reveal the current shell
                while (randomShell < 0 || randomShell == gun.getRounds()) {
                    randomShell = Randomness.getRandomInt(1, gun.getRounds()); // This is a RANDOM shell
                }
                // int shell = randomShell + 1; // This is the way we are going to talk about the next shell
                // if (gun.find(randomShell)) { // If it is live
                // System.out.println("SHELL #" + shell + "... LIVE ROUND...");
                // } else { // If it is blank
                // System.out.println("SHELL #" + shell + "... BLANK ROUND...");
                // }
                gun.addToKnownRounds(randomShell); // Add the round to a list of known rounds to be use later
            }
        }
    }

    // Flip a coin to determine if the medicine does 1 damage or heals 2
    private static void takeMedicine(Player user) {
        int random = Randomness.getRandomInt(0, 2); // 0 or 1
        if (random == 0) {
            user.decreaseHealth(1);
        } else {
            user.increaseHealth(2);
        }
    }

    // Look at the current shell
    private static boolean peek(Gun gun, Player user) {
        if (user instanceof Dealer) {
            // The dealer peeking should not print anything
            boolean peek = gun.peek(); // Take a peek into the gun
            return peek; // Let me use it for extra purposes if I need
        } else {
            // The player taking a peek should print something
            boolean peek = gun.peek(); // Take a peek into the gun
            if (peek) {
                System.out.println("LIVE ROUND");
            } else {
                System.out.println("BLANK ROUND");
            }
            return peek; // Let me use it for extra purposes if I need
        }
    }

    // Set the dealer to know things if he uses GLASS
    private static void setDealerKnowledge(Dealer dealer, boolean shell) {
        // And set variables accordingly
        dealer.setKnowsCurrentShell(true);
        dealer.setCurrentShell(shell);
    }

    private static void beer(Gun gun) {
        // Eject current shell
        boolean ejectedShell = gun.ejectCurrentShell(); // This returns the polarity of the shell you just ejected
        if (ejectedShell) {
            System.out.println("Ejected live round.");
        } else {
            System.out.println("Ejected blank round.");
        }
    }

    private static void stealFrom(Gun gun, Player robber, Player victim) {
        if (!(robber instanceof Dealer)) {
            int toSteal = UserInput.getItem(victim); // Ask the user which item to steal
            String itemStolen = victim.getItems()[toSteal]; // Save this value for later
            // Announce the action
            System.out.println(robber.getName() + " stole " + itemStolen + " from " + victim.getName() + ".");
            // Then actually do it
            robber.addItem(itemStolen);
            victim.removeItem(toSteal);
        } else {
            // This is the code for dealer
            Dealer realDeal = (Dealer)robber;
            String toSteal = realDeal.getWantsToSteal();
            // Announce the stolen item
            System.out.println(robber.getName() + " stole " + toSteal + " from " + victim.getName() + ".");
            int playersItem = victim.has(toSteal); // This will always yield an item
            robber.addItem(toSteal);
            victim.removeItem(playersItem);
        }
        // Force the player to use the item
        useItem(robber.getItems().length - 1, gun, robber, victim); // This uses the most recently added item, which is the one they just stole
    }
}
