package arsylk.cardgame;

import java.util.Scanner;

public class Textout {
    public static final int LOG_SPECIAL = -2, LOG_NONE = 0, LOG_NORMAL = 1, LOG_DEBUG = 2, LOG_ALL = 3;
    private static final int LOG = LOG_DEBUG;

    public static boolean write(String msg, int lvl) {
        if(LOG < lvl)
            return false;
        else {
            System.out.println(msg);
            return true;
        }
    }

    public static int getInt(String prompt) {
        System.out.print(prompt);
        while(true){
            try {
                return Integer.parseInt(new Scanner(System.in).next());
            } catch(NumberFormatException ne) {
                System.out.print("That's not a whole number.\n"+prompt);
            }
        }
    }
}
