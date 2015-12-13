package custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jmprathab on 10/12/15.
 */
public class ScrollingTextView extends TextView {

    public ScrollingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        rotate();
    }

    public ScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        rotate();
    }

    public ScrollingTextView(Context context) {
        super(context);
        init();
        rotate();
    }

    private void rotate() {
        setSelected(true);
    }

    private void init() {
        if (!isInEditMode()) {

        }
    }


}
