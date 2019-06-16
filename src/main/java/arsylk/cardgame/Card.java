package arsylk.cardgame;

public class Card {
    private int suit, figure;
    private boolean marked = false;
    private static final String tSuit[] = {"Hearts","Spades","Diamonds","Clubs"};
    private static final String tFigure[] = {"Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King"};
    public Card(int suit, int figure) {
        this.suit = suit;
        this.figure = figure;
    }

    public int getSuit() {
        return this.suit;
    }

    public int getFigure() {
        return this.figure;
    }

    public void markForRemoval() {
        this.marked = true;
    }

    public boolean isMarked() {
        return this.marked;
    }

    public boolean isActive() {
        return (figure < 5 || figure == 10);
    }

    public static String translateSuit(int suit) {
        return tSuit[suit];
    }

    public static String translateFigure(int figure) {
        return tFigure[figure];
    }

    @Override
    public String toString() {
        return tFigure[figure] + " of " + tSuit[suit];
    }
}