package arsylk.cardgame.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import arsylk.cardgame.R;

public class CardView extends PercentRelativeLayout {
    private View rootView;
    private TextView[] texFigures = new TextView[2];
    private boolean show = true;
    private int size = 0;
    private int suit = 0, figure = 0;


    public CardView(Context context) {
        super(context);
        init(context);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardView, 0, 0);
        try {
            size = a.getDimensionPixelSize(R.styleable.CardView_size, 0);
            show = a.getBoolean(R.styleable.CardView_show, true);
            suit = a.getInt(R.styleable.CardView_suit, 0);
            figure = a.getInt(R.styleable.CardView_figure, 0);
        }finally {
            a.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.view_card, this);
        texFigures[0] = (TextView) rootView.findViewById(R.id.text_figure_up);
        texFigures[1] = (TextView) rootView.findViewById(R.id.text_figure_down);
        for(TextView t : texFigures) {
            t.setText(String.valueOf(figure));
            t.setTextSize(size);
        }
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
        fixAfter();
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
        fixAfter();
    }

    public int getFigure() {
        return figure;
    }

    public void setFigure(int figure) {
        this.figure = figure;
        fixAfter();
    }

    private void fixAfter() {
        invalidate();
        requestLayout();
    }
}
