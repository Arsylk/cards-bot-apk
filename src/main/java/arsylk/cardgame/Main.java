package arsylk.cardgame;


import java.util.ArrayList;
import java.util.List;

public class Main {
/*
    public static void main(String[] args) {
	        new Main(1);
    }

    public Main(int zzz) {
        System.out.println("\n#######Game "+zzz+"#######\n");

        startGame(2);
    }

    private int startGame(int players) {
        //init table and deck
        Deck deck = new Deck();
        deck.shuffle();
        Table table = new Table(deck, players);

        //init and deal hands
        Hand hands[] = new Hand[players];
        for(int i = 0; i < hands.length; i++) {
            hands[i] = new Hand();
            if(i == 0) {
                List<Card> debug = new ArrayList<>();
                debug.add(new Card(2, 9));
                debug.add(new Card(3, 9));
                debug.add(new Card(0, 9));
                debug.add(new Card(0, 5));
                debug.add(new Card(2, 5));
                hands[i] = new Hand(debug, );
            }else
            hands[i].initHand(deck, 5);
            Textout.write("Hand"+(i+1)+": "+hands[i]+"\n", Textout.LOG_DEBUG);
        }
        hands[0].mine = true;

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

        //init game loop
        while(true) {
            for(int i = 0; i < hands.length; i++) {
                //click(40,880);
                Textout.write("Table: "+table.getFirst()+" "+table.debugDisplay(), Textout.LOG_NORMAL);
                System.out.println();
                Textout.write("Hand"+i+": "+hands[i], (hands[i].mine) ? Textout.LOG_NORMAL : Textout.LOG_DEBUG);
                hands[i].move(deck, table);
                if(hands[i].size() == 0) return i;
                System.out.println();
            }
        }
    }
    */
}
