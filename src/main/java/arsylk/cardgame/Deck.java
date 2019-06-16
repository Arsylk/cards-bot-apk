package arsylk.cardgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cardList;
    private List<Card> usedCardList;

    public Deck() {
        init();
    }

    private void init() {
        cardList = new ArrayList<>();
        usedCardList = new ArrayList<>();
        for(int iSuit = 0; iSuit < 4; iSuit++) {
            for(int iFigure = 0; iFigure < 13; iFigure++) {
                cardList.add(new Card(iSuit, iFigure));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cardList);
    }

    public Card getFirst() {
        if(cardList.size() == 0) {
            cardList = usedCardList;
            shuffle();
        }
        Card first = cardList.get(cardList.size()-1);
        cardList.remove(cardList.size()-1);
        return first;
    }

    public void putBack(Card card) {
        usedCardList.add(card);
    }

    public int getSize() {
        return cardList.size();
    }
}