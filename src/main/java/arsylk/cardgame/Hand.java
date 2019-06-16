package arsylk.cardgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import arsylk.cardgame.Interfaces.GameEvent;

public class Hand {
    private List<Card> cardList;
    private boolean userControled = false;
    private GameEvent.onHandUpdate onHandUpdate;

    public Hand(boolean userControled) {
        init(userControled);
    }

    public Hand(List<Card> cardList, boolean userControled) {
        init(userControled);
        this.cardList = cardList;
    }

    private void init(boolean userControled) {
        this.userControled = userControled;
        cardList = new ArrayList<>();
    }

    public void setOnHandUpdate(GameEvent.onHandUpdate onHandUpdate) {
        this.onHandUpdate = onHandUpdate;
    }

    private void addCard(Card card) {
        cardList.add(card);
    }

    public void initHand(Deck deck, int size) {
        for(int i = 0; i < size; i++) {
            addCard(deck.getFirst());
        }
    }

    public void drawCards(Deck deck, int size) {
        for(int i = 0; i < size; i++) {
            addCard(deck.getFirst());
        }
        for(int i = 0; i < size; i++) {
            Textout.write("Draws: "+cardList.get(cardList.size()-1-i), Textout.LOG_DEBUG);
        }
    }

    public List<Integer> multipleCards(int figure) {
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < cardList.size(); i++) {
            if(cardList.get(i).getFigure() == figure)
                indexes.add(i);
        }

        return indexes;
    }

    private List<Integer> sortSuit(Table table, List<Integer> indexes) {
        for(int i = 0; i < indexes.size(); i++) {
            //check if block queen
            Card card = cardList.get(indexes.get(i));
            if ((table.getDraw() != 0 || table.getWait() != 0) && card.getFigure() == 11) {
                if ((card.getSuit() < 2) && !(cardList.get(indexes.get(0)).getSuit() < 2)) {
                    int temp = indexes.get(0);
                    indexes.set(0, indexes.get(i));
                    indexes.set(i, temp);
                }
            }
            //check if request suit
            else if (table.getSuit() != -1) {
                if (card.getSuit() == table.getSuit()) {
                    int temp = indexes.get(0);
                    indexes.set(0, indexes.get(i));
                    indexes.set(i, temp);
                }
            }
            //normal cards
            else if (card.getSuit() == table.getFirst().getSuit() && (table.getFirst().getSuit() != cardList.get(indexes.get(0)).getSuit())) {
                int temp = indexes.get(0);
                indexes.set(0, indexes.get(i));
                indexes.set(i, temp);
            }
        }
        return indexes;
    }

    private List<Integer> findHighestMultiple(boolean ignoreActive) {
        int highest = 0;
        List<List<Integer>> multipleHighest = new ArrayList<>();
        for (Card card : cardList) {
            //skip active cards
            if (card.isActive() && ignoreActive) continue;
            List<Integer> tempIndexes = multipleCards(card.getFigure());
            if (tempIndexes.size() >= highest) {
                if (tempIndexes.size() > highest) {
                    multipleHighest.clear();
                    highest = tempIndexes.size();
                }
                multipleHighest.add(tempIndexes);
            }
        }
        //fail-safe no options
        if(multipleHighest.size() != 0)
            return multipleHighest.get(new Random().nextInt(multipleHighest.size()));
        else
            return new ArrayList<>();
    }

    private void useJack(Table table) {
        List<Integer> indexesMultiple = findHighestMultiple(true);
        if(indexesMultiple.size() == 0) {
            table.setFigure(-1);
        }else {
            table.setFigure(cardList.get(indexesMultiple.get(0)).getFigure());
        }
    }

    private void useAce(Table table) {
        //count each suit in hand
        int suitCount[] = new int[4];
        for (Card card : cardList) {
            suitCount[card.getSuit()]++;
        }
        //find which suit is optimal
        int suitMax = 0;
        List<Integer> multipleSuits = new ArrayList<>();
        for(int i = 0; i < suitCount.length; i++) {
            if(suitCount[i] >= suitMax) {
                if(suitCount[i] > suitMax) {
                    multipleSuits.clear();
                    suitMax = suitCount[i];
                }
                multipleSuits.add(i);
            }
        }
        //fail-safe no options
        if(multipleSuits.size() != 0)
            table.setSuit(multipleSuits.get(new Random().nextInt(multipleSuits.size())));
        else
           table.clearSuit();
    }

    private void performMove(Table table, List<Integer> indexes) {
        //sort suits if multiple cards
        if(indexes.size() > 1 && table.getFigure() == -1) {
            indexes = sortSuit(table, indexes);
        }
        //determinate best figure request if jack is used
        if(cardList.get(indexes.get(0)).getFigure() == 10) {
            useJack(table);
        }
        //determinate best suit request if ace is used
        if(cardList.get(indexes.get(0)).getFigure() == 0) {
            useAce(table);
        }
        placeAndUpdate(table, indexes);
    }

    public void placeAndUpdate(Table table, int index) {
        placeAndUpdate(table, Arrays.asList(index));
    }

    public void placeAndUpdate(Table table, List<Integer> indexes) {
        //place and update
        List<Card> newCardList = new ArrayList<>();

        //place the cards
        for (Integer index : indexes) {
            Card card = cardList.get(index);
            table.putCard(card);
            card.markForRemoval();
            Textout.write("Puts: " + card, Textout.LOG_NORMAL);
        }
        //update hand
        for(int i  = 0; i < cardList.size(); i++) {
            Card card = cardList.get(i);
            if(!indexes.contains(i)) {
                newCardList.add(card);
            }
        }
        cardList = newCardList;
        if(onHandUpdate != null && isUserControled()) onHandUpdate.onHandUpdate(cardList, userControled);
    }

    public List<Integer> possibleMoves(Table table) {
        List<Integer> options = new ArrayList<>();
        for(int i = 0; i  < cardList.size(); i++) {
            Card card = cardList.get(i);
            if(table.canPutCard(card)) options.add(i);
        }
        return options;
    }

    public void endMove(Table table, boolean successful) {
        if(table.getFirst().getFigure() != 0) table.setSuit(-1);
        if((table.getFirst().getFigure() != 10 || !successful) && table.getFigure() != -1) table.clearFigure();

        if(successful && cardList.size() == 0) {
            if(table.getFirst().isActive())
                drawCards(table.getDeck(), 1);
            else
                Textout.write("Wins", Textout.LOG_NORMAL);
        }else if(!successful && table.isActive()) {
            //draw cards and clear draw pool
            if(table.getDraw() != 0) {
                if(waitTurns == 0) drawCards(table.getDeck(), table.getDraw()-1);
                table.clearDraw();
            }
            //set wait turns and clear wait
            else if(table.getWait() != 0) {
                waitTurns += table.getWait()-1;
                table.clearWait();
                Textout.write("Waits " + waitTurns + " more turns", Textout.LOG_NORMAL);
            }
            //clear suit
            else if(table.getSuit() != -1 && table.getFirst().getFigure() != 0) {
                table.clearSuit();
            }
        }else if(!successful && waitTurns != 0) {
            waitTurns--;
        }
        if(onHandUpdate != null && isUserControled()) onHandUpdate.onHandUpdate(getCardsInHand(), isUserControled());
    }

    public int waitTurns = 0;
    public void move(Deck deck, Table table, boolean... afterDraw) {
        List<Integer> options = possibleMoves(table);
        //wait turns
        if(waitTurns != 0) {
            endMove(table, false);
            Textout.write("Waits " + waitTurns + " more turns", Textout.LOG_NORMAL);
            return;
        }
        //display possible moves
        Textout.write("Possible moves: "+ options.size(), Textout.LOG_DEBUG);
        for (int i = 0; i < options.size(); i++) {
            Integer option = options.get(i);
            Textout.write("Possible move: " + cardList.get(option), Textout.LOG_DEBUG);
        }
        //handle no possible moves
        if(options.size() == 0) {
            //handle first check
            if(afterDraw.length == 0) {
                //handle waiting
                if(table.getWait() != 0) {
                    endMove(table, false);
                }else {
                    //draw first card and check for possible moves
                    drawCards(deck, 1);
                    move(deck, table, true);
                }
            }
            //handle second check
            else {
                endMove(table, false);
            }
        }
        //handle possible move(s)
        else {
            int big = 0;
            List<Integer> newIndexes = new ArrayList<>();
            for (Integer option : options) {
                List<Integer> indexes = multipleCards(cardList.get(option).getFigure());
                if (indexes.size() >= big) {
                    if (indexes.size() > big) {
                        big = indexes.size();
                        newIndexes.clear();
                    }
                    newIndexes.add(option);
                }
            }
            performMove(table, multipleCards(cardList.get(newIndexes.get(new Random().nextInt(newIndexes.size()))).getFigure()));
            endMove(table, true);
        }
    }

    public List<Card> getCardsInHand() {
        return cardList;
    }

    public int size() {
        return cardList.size();
    }

    public boolean isUserControled() {
        return userControled;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for(Card card : cardList) {
            output.append(card).append(", ");
        }
        return output.substring(0, Math.max(0, output.length()-3));
    }
}