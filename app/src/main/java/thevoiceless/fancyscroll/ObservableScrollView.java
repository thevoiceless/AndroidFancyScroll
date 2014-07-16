package thevoiceless.fancyscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Credit goes to http://stackoverflow.com/a/3952629/1693087
 */
public class ObservableScrollView extends ScrollView {

    private ScrollViewListener onScrollListener;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnScrollListener(ScrollViewListener listener) {
        onScrollListener = listener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);

        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(this, x, y, oldX, oldY);
        }
    }
}
