package arsylk.cardgame.Interfaces;

import arsylk.cardgame.Card;
import arsylk.cardgame.Table;

import java.util.List;


public interface GameEvent {
    interface onTableUpdate {
        void onTableUpdate(Card card);
    }
    interface onHandUpdate {
        void onHandUpdate(List<Card> cardList, boolean userControled);
    }
    interface onPickCard {
        int onPickCard(List<Integer> allowedCards);
    }
    interface indicateDraw {
        void indicateDraw();
    }
}
