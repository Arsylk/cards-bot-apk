package arsylk.cardgame;

import java.util.ArrayList;
import java.util.List;

import arsylk.cardgame.Interfaces.GameEvent;

public class Table {
    private List<Card> cardList;
    private Deck deck;
    private int players;
    private int counter;
    private boolean skip = false;
    private int draw = 0, wait = 0, figure = -1, suit = -1;

    private GameEvent.onTableUpdate onTableUpdate;

    public Table(Deck deck, int players) {
        this.deck = deck;
        this.players = players;
        init();
    }

    private void init() {
        cardList = new ArrayList<>();
    }

    public void setOnTableUpdate(GameEvent.onTableUpdate onTableUpdate) {
        this.onTableUpdate = onTableUpdate;
    }

    public boolean canPutCard(Card card) {
        //non-active card
        if(!isActive()) {
            if(getFirst().getSuit() == card.getSuit() || getFirst().getFigure() == card.getFigure() || card.getFigure() == 4 || getFirst().getFigure() == 4)
                return true;
        }
        //active cards
        else {
            //draw cards
            if(getDraw() != 0) {
                if ((card.getFigure() == 11 && (card.getSuit() == 0 || card.getSuit() == 1)) || getFirst().getFigure() == card.getFigure() ||
                        (getFirst().getSuit() == card.getSuit() && (card.getFigure() == 1 || card.getFigure() == 2))) {
                    return true;
                }
            }
            //wait turns
            else if(getWait() != 0) {
                if((card.getFigure() == 11 && (card.getSuit() == 0 || card.getSuit() == 1)) || getFirst().getFigure() == card.getFigure()) {
                    return true;
                }
            }
            //request figures
            else if(getFigure() != -1) {
                if(card.getFigure() == getFigure() || card.getFigure() == 10) {
                    return true;
                }
            }
            //request suit
            else if(getSuit() != -1) {
                if(card.getSuit() == getSuit() || card.getFigure() == 0 || card.getFigure() == 4 || getFirst().getFigure() == card.getFigure()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void putCard(Card card) {
        if(onTableUpdate != null) onTableUpdate.onTableUpdate(card);
        cardList.add(card);
        deck.putBack(card);
        if(card.getFigure() == 1 || card.getFigure() == 2) {
            draw+=card.getFigure()+1;
        }else if(card.getFigure() == 3) {
            wait++;
        }else if((card.getFigure() == 11 && (card.getSuit() == 0 || card.getSuit() == 1))) {
            draw = 0;
            wait = 0;
        }
    }

    public Card getFirst() {
        return cardList.get(cardList.size()-1);
    }

    public int getDraw() {
        return draw;
    }

    public void clearDraw() {
        draw = 0;
    }

    public int getWait() {
        return wait;
    }

    public void clearWait() {
        wait = 0;
    }

    public int getFigure() {
        return figure;
    }

    public void setFigure(int figure) {
        this.figure = figure;
        this.counter = players-1;
    }

    public void clearFigure() {
        if(counter > 0) {
            counter--;
        }else {
            figure = -1;
        }
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public void clearSuit() {
        this.suit = -1;
    }

    public boolean skip() {
        if(skip) {
            skip = false;
            return true;
        }else {
            return skip;
        }
    }

    public void skipNext() {
        skip = true;
    }

    public boolean isActive() {
        return (draw != 0 || wait != 0 || figure != -1 || suit != -1);
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getUsedCardList() {
        return cardList;
    }

    public void clear() {
        cardList.clear();
    }

    public String debugDisplay() {
        return "[" +
                ((draw != 0) ? " Draw: " + draw : "") +
                ((wait != 0) ? " Wait: " + wait : "") +
                ((figure != -1) ? " Figure: " + Card.translateFigure(figure) : "") +
                ((suit != -1) ? " Suit: " + Card.translateSuit(suit) : "") + " ]";
    }
}
