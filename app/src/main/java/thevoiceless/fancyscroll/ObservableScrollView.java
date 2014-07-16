package thevoiceless.fancyscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Created by riley on 7/14/14.
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

    public void setOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);

        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(this, x, y, oldX, oldY);
        }
    }
}
