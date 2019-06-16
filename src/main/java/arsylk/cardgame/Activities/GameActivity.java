package arsylk.cardgame.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import arsylk.cardgame.Card;
import arsylk.cardgame.Deck;
import arsylk.cardgame.Hand;
import arsylk.cardgame.Interfaces.GameEvent;
import arsylk.cardgame.R;
import arsylk.cardgame.Table;

public class GameActivity extends AppCompatActivity implements GameEvent.onTableUpdate, GameEvent.onHandUpdate, GameEvent.indicateDraw {
    //constants
    private  static final int players = 2;
    //game objects
    private Deck deck;
    private Table table;
    private Hand userHand;
    private Hand enemyHand;
    //guit elements
    private LinearLayout user_hand, enemy_hand;
    private LinearLayout user_hand_rows[], enemy_hand_rows[];
    private ImageView top_cards[];

    private void initViews() {
        View draw_deck = findViewById(R.id.image_drawdeck);
        assert draw_deck != null;
        draw_deck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawACard();
            }
        });
        draw_deck.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                endMyTurn();
                return true;
            }
        });

        top_cards = new ImageView[4];
        top_cards[3] = (ImageView) findViewById(R.id.image_topcard3);
        top_cards[2] = (ImageView) findViewById(R.id.image_topcard2);
        top_cards[1] = (ImageView) findViewById(R.id.image_topcard1);
        top_cards[0] = (ImageView) findViewById(R.id.image_topcard);

        user_hand = (LinearLayout) findViewById(R.id.layout_user_hand);
        enemy_hand = (LinearLayout) findViewById(R.id.layout_enemy_hand);

        user_hand_rows = new LinearLayout[3];
        //user_hand_rows[0] = (LinearLayout) findViewById(R.id.layout_user_row1);
        //user_hand_rows[1] = (LinearLayout) findViewById(R.id.layout_user_row2);
        //user_hand_rows[2] = (LinearLayout) findViewById(R.id.layout_user_row3);
    }

    private void initGame() {
        //init table and deck
        deck = new Deck();
        deck.shuffle();
        table = new Table(deck, players);
        table.setOnTableUpdate(this);

        //user hand
        userHand = new Hand(true);
        userHand.drawCards(deck, 5);
        userHand.setOnHandUpdate(this);
        //enemy hand
        List<Card> cl = new ArrayList<>();
        cl.add(new Card(0,0));
        cl.add(new Card(1,0));
        cl.add(new Card(2,0));
        cl.add(new Card(3,0));
        cl.add(new Card(3,6));
        cl.add(new Card(3,7));
        enemyHand = new Hand(false);
        enemyHand.drawCards(deck, 5);

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
        this.onHandUpdate(userHand.getCardsInHand(), userHand.isUserControled());
        this.onHandUpdate(enemyHand.getCardsInHand(), enemyHand.isUserControled());
    }

    private void initGameOver(final boolean win) {
        new AlertDialog.Builder(GameActivity.this).setTitle(win ? "You won!" : "You lost!").setMessage(win ? "Enjoy your victory" : "Enjoy your demise").setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initViews();
        initGame();
    }


    private void selectCard(int picked) {
        Card card = new Card(-1,-1);
        View view = user_hand.getChildAt(picked);
        if(view.getTag() instanceof Card)
            card = (Card) view.getTag();

        if(view.isActivated()) {
            userMove(picked);
        }else if(table.canPutCard(card) && (pickedFigure == -1 || pickedFigure == card.getFigure())) {
            for(int i = 0; i < user_hand.getChildCount(); i++) {
                if(user_hand.getChildAt(i).isActivated()) {
                    TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_SELF, -0.0f);
                    ta.setDuration(500);
                    ta.setFillAfter(true);
                    user_hand.getChildAt(i).startAnimation(ta);
                    user_hand.getChildAt(i).setActivated(false);
                }
            }
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.1f);
            ta.setDuration(500);
            ta.setFillAfter(true);
            view.startAnimation(ta);
            view.setActivated(true);
        }else this.indicateDraw();
    }

    //every time user clicks a card
    private boolean userTurn = true;
    private boolean hasDrawn = false;
    private int pickedFigure = -1;
    public void userMove(int picked) {
        boolean startedEnemyMove = false;
        Card card = userHand.getCardsInHand().get(picked);
        //place picked card
        if(table.canPutCard(card) && (pickedFigure == -1 || pickedFigure == card.getFigure())) {
            userHand.placeAndUpdate(table, picked);
            //check if more moves in same turn
            if(userHand.multipleCards(card.getFigure()).size() == 0) {
                userTurn = false;
                userHand.endMove(table, true);
                if(card.getFigure() == 0) {
                    startedEnemyMove = true;
                    userUseAce();
                }else if(card.getFigure() == 10) {
                    startedEnemyMove = true;
                    userUseJack();
                }
            }else pickedFigure = card.getFigure();
        }
        //if cant place picked card
        else {
            if(hasDrawn || table.getWait() != 0) {
                userHand.endMove(table, false);
                userTurn = false;
            }else {
                this.indicateDraw();
            }
        }
        this.onHandUpdate(userHand.getCardsInHand(), true);
        ((TextView) findViewById(R.id.action_text)).setText(table.debugDisplay());
        if(!userTurn && !startedEnemyMove)
            animatedEnemyMove(true);
    }

    //after user click draw deck
    public void drawACard() {
        if(!hasDrawn && table.getWait() == 0 && pickedFigure == -1) {
            hasDrawn = true;
            userHand.drawCards(deck, 1);
            this.onHandUpdate(userHand.getCardsInHand(), true);
            //check if any move is possible after drawing
            if(userHand.possibleMoves(table).size() == 0) {
                userTurn = false;
                userHand.endMove(table, false);
                animatedEnemyMove(true);
            }
        }else {
            endMyTurn();
        }
    }

    //after user long click draw deck
    public void endMyTurn() {
        if(table.getDraw() != 0 || table.getWait() != 0) {
            userTurn = false;
            userHand.endMove(table, false);
            animatedEnemyMove(true);
        }else if(hasDrawn || pickedFigure != -1) {
            userTurn = false;
            userHand.endMove(table, (pickedFigure != -1));
            animatedEnemyMove(true);
        }else {
            indicateDraw();
        }
    }

    //after user uses ace
    private void userUseAce() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(GameActivity.this);
        builderSingle.setTitle("Select suit");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_singlechoice, new String[]{"Hearts", "Spades", "Diamonds", "Clubs"});
        builderSingle.setNegativeButton("None", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                animatedEnemyMove(true);
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table.setSuit(which);
                animatedEnemyMove(true);
            }
        });
        builderSingle.show();
    }

    //after user use jack
    private void userUseJack() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(GameActivity.this);
        builderSingle.setTitle("Select figure");
        final ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_singlechoice, new Integer[]{6,7,8,9,10});
        builderSingle.setNegativeButton("None", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                animatedEnemyMove(true);
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table.setFigure(arrayAdapter.getItem(which)-1);
                animatedEnemyMove(true);
            }
        });
        builderSingle.show();
    }

    //after user turn
    private void animatedEnemyMove(final boolean shouldEnemyMove) {
        userTurn = false;
        if(userHand.size() == 0 || enemyHand.size() == 0)
            initGameOver(userHand.size() == 0);
        else user_hand.animate().alpha(shouldEnemyMove ? 0.0f : 1.0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(shouldEnemyMove) {
                    //perform enemy move
                    enemyMove();
                    animatedEnemyMove(false);
                }else {
                    //skip if has to wait
                    if((table.getWait() != 0 && userHand.possibleMoves(table).size() == 0) || userHand.waitTurns != 0) {
                        userHand.endMove(table, false);
                        animatedEnemyMove(true);
                    }else {
                        //reset user state
                        hasDrawn = false;
                        userTurn = true;
                        GameActivity.this.onHandUpdate(userHand.getCardsInHand(), userHand.isUserControled());
                    }
                }
            }
        });
    }

    //after animation of enemy turn
    private void enemyMove() {
        pickedFigure = -1;
        enemyHand.move(deck, table);
        ((TextView) findViewById(R.id.action_text)).setText(table.debugDisplay());
        this.onHandUpdate(enemyHand.getCardsInHand(), false);
    }

    //update table every time new card is put
    @Override
    public void onTableUpdate(Card card) {
        top_cards[3].setImageDrawable(top_cards[2].getDrawable());
        top_cards[2].setImageDrawable(top_cards[1].getDrawable());
        top_cards[1].setImageDrawable(top_cards[0].getDrawable());
        top_cards[0].setImageDrawable(getDrawableFromCard(card));
    }

    //update when every time hand changes
    @Override
    public void onHandUpdate(List<Card> cardList, boolean userControled) {
        //select hand and clear
        LinearLayout hand = userControled ? user_hand : enemy_hand;
        hand.removeAllViewsInLayout();
        for(Card card : cardList) {
            ImageView cardView = cardIntoLayout(hand, userControled ? card : new Card(-1, -1), (table.canPutCard(card) && (pickedFigure == -1 || pickedFigure == card.getFigure())));
            if(userControled) {
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getTag() instanceof Card || userTurn) {
                            selectCard(user_hand.indexOfChild(v));
                        }
                    }
                });
            }
        }

    }

    //animate deck to indicate draw
    @Override
    public void indicateDraw() {
        ScaleAnimation sa = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setRepeatMode(Animation.REVERSE);
        sa.setRepeatCount(1);
        sa.setDuration(300);
        findViewById(R.id.image_drawdeck).startAnimation(sa);
    }

    private Drawable getDrawableFromCard(Card card) {
        if(card.getFigure() == -1 && card.getSuit() == -1) return getResources().getDrawable(R.drawable.revers);
        return getResources().obtainTypedArray((new int[] {R.array.heart_cards, R.array.spade_cards, R.array.diamond_cards, R.array.club_cards})[card.getSuit()]).getDrawable(card.getFigure());
    }

    private ImageView cardIntoLayout(LinearLayout hand, Card card, boolean canPut) {
        ImageView cardView = new ImageView(this);
        cardView.setImageDrawable(getDrawableFromCard(card));
        cardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        cardView.setPadding(3,3,3,3);
        cardView.setAdjustViewBounds(true);
        cardView.setMaxHeight((int)(120 * getResources().getDisplayMetrics().density + 0.5f));
        cardView.setTag(card);
        if((!canPut || !userTurn) && (card.getFigure() != -1 && card.getSuit() != -1)) {
            cardView.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        hand.addView(cardView);

        return cardView;
    }

}
