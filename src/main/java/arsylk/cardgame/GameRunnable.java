package arsylk.cardgame;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import arsylk.cardgame.Interfaces.GameEvent;

public class GameRunnable implements Runnable {
    private Context context;
    private int players;
    private GameEvent.onTableUpdate onTableUpdate;
    private  GameEvent.onHandUpdate onHandUpdate;
    private  GameEvent.indicateDraw indicateDraw;

    private Table table;
    private Deck deck;
    private Hand userHand;
    private Hand enemyHand;


    public GameRunnable(Context context, int players, GameEvent.onTableUpdate onTableUpdate, GameEvent.onHandUpdate onHandUpdate, GameEvent.indicateDraw indicateDraw) {
        this.context = context;
        this.players = players;
        this.onTableUpdate = onTableUpdate;
        this.onHandUpdate = onHandUpdate;
        this.indicateDraw = indicateDraw;
    }

    private void init() {
        //init table and deck
        deck = new Deck();
        deck.shuffle();
        table = new Table(deck, players);
        table.setOnTableUpdate(onTableUpdate);

        //init and deal hands
        Hand hands[] = new Hand[players];
        for(int i = 0; i < hands.length; i++) {
            hands[i] = new Hand(i == 0);
            if(hands[i].isUserControled()) userHand = hands[i];
            hands[i].drawCards(deck, 5);
            Textout.write("Hand"+(i+1)+": "+hands[i]+"\n", Textout.LOG_DEBUG);
        }
        enemyHand = hands[1];

        //put first non-active card
        while(true) {
            Card card = deck.getFirst();
            if(card.isActive())
                deck.putBack(card);
            else {
                table.putCard(card);
                break;
            }
        }
        onHandUpdate.onHandUpdate(hands[0].getCardsInHand(), hands[0].isUserControled());
        onHandUpdate.onHandUpdate(hands[1].getCardsInHand(), hands[1].isUserControled());
    }

    public Table getTable() {
        return table;
    }


    @Override
    public void run() {
        init();
    }

    private boolean userTurn = true;
    private boolean hasDrawn = false;
    public void userMove(int picked) {
        Card card = userHand.getCardsInHand().get(picked);
        //place picked card
        if(table.canPutCard(card)) {
            userHand.placeAndUpdate(table, picked);
            //check if more moves in same turn
            if(userHand.multipleCards(card.getFigure()).size() == 0) {
                userTurn = false;
            }
        }
        //if cant place picked card
        else {
            if(hasDrawn || table.getWait() != 0) {
                userHand.endMove(table, false);
                userHand.waitTurns = table.getWait();
                userTurn = false;
            }else {
                indicateDraw.indicateDraw();
            }
        }
        //onHandUpdate.onHandUpdate(userHand.getCardsInHand(), true);
        if(!userTurn)
            new AlertDialog.Builder(context).setMessage("End turn").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    enemyMove();
                }
            }).create().show();
    }

    private void enemyMove() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" ");
        System.out.println("Start enemy move");
        onHandUpdate.onHandUpdate(userHand.getCardsInHand(), true);
        enemyHand.move(deck, table, false);
        onHandUpdate.onHandUpdate(enemyHand.getCardsInHand(), false);
        hasDrawn = false;
        userTurn = true;
    }

    public void drawACard() {
        if(!hasDrawn) {
            hasDrawn = true;
            userHand.drawCards(deck, 1);
            onHandUpdate.onHandUpdate(userHand.getCardsInHand(), true);
        }
    }
}
