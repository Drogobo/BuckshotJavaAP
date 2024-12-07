
/**
 * Write a description of class Randomness here.
 *
 * DROGOBO
 */
public class Randomness
{
    // Everything in this class is just to aid in generating random numbers
    public static int getRandomInt(int start, int end) {
        // Second value is exclusive btw
        return (int)(Math.random() * (end - start) + start);
    }
}
