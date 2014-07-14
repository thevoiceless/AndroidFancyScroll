package thevoiceless.fancyscroll;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;


public class MainActivity extends Activity implements ScrollViewListener {

    private ImageView cover;
    private Drawable bg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
        bg = getResources().getDrawable(R.drawable.ab);
        bg.setAlpha(0);
        getActionBar().setBackgroundDrawable(bg);

        final ObservableScrollView scrollView = ((ObservableScrollView) findViewById(R.id.scroll));
        cover = ((ImageView) findViewById(R.id.cover));

        scrollView.setScrollViewListener(this);
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
        float percentHidden = (scrollView.getScrollY() / (float) cover.getHeight());
        bg.setAlpha(mapHiddenPercentToAlpha(percentHidden));
        getActionBar().setBackgroundDrawable(bg);

        int top = scrollView.getScrollY() / 2;
        cover.setPadding(0, top, 0, 0);
    }

    private int mapHiddenPercentToAlpha(float percentHidden) {
        if (percentHidden < 0f) {
            percentHidden = 0f;
        }
        if (percentHidden > 1f) {
            percentHidden = 1f;
        }

        return Math.round(percentHidden * 255);
    }
}
