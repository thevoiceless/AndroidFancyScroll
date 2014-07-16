package thevoiceless.fancyscroll;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.WindowCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;


public class MainActivity extends Activity implements ScrollViewListener {

    private static final String KEY_SCROLL_POS = "KEY_SCROLL_POS";

    private static int actionBarOpacity = 0;

    private DrawerLayout drawer;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private ObservableScrollView scrollView;
    private ImageView cover;
    private Drawable actionBarBG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        actionBarBG = getResources().getDrawable(R.drawable.ab);
        drawer = ((DrawerLayout) findViewById(R.id.drawer));
        drawerList = ((ListView) findViewById(R.id.drawer_list));
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                actionBarBG.setAlpha(mapFractionToAlpha(slideOffset, actionBarOpacity));
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        scrollView = ((ObservableScrollView) findViewById(R.id.scroll));
        cover = ((ImageView) findViewById(R.id.cover));

        scrollView.setOnScrollListener(this);
        setActionBarOpacity(actionBarOpacity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setImageViewScaling();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCROLL_POS, scrollView.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int position = savedInstanceState.getInt(KEY_SCROLL_POS);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, position);

                if (drawer.isDrawerOpen(drawerList)) {
                    actionBarBG.setAlpha(mapFractionToAlpha(1f, actionBarOpacity));
                }
            }
        });

//        if (scrollView.getViewTreeObserver().isAlive()) {
//            scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    scrollView.scrollTo(0, position);
//
//                    if (scrollView.getViewTreeObserver().isAlive()) {
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                            scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                        } else {
//                            scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        }
//                    }
//                }
//            });
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
        float percentHidden = (scrollView.getScrollY() / (float) cover.getHeight());
        setActionBarOpacity(mapFractionToAlpha(percentHidden));

        int top = scrollView.getScrollY() / 2;
        cover.setPadding(0, top, 0, 0);
    }

    private void setImageViewScaling() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180) {
            // Portrait, use "center"
            cover.setScaleType(ImageView.ScaleType.CENTER);
        } else {
            // Landscape, use "centerCrop"
            cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void setActionBarOpacity(int opacity) {
        actionBarOpacity = opacity;
        actionBarBG.setAlpha(actionBarOpacity);
        getActionBar().setBackgroundDrawable(actionBarBG);
    }

    private int mapFractionToAlpha(float alphaFraction) {
        if (alphaFraction < 0f) {
            alphaFraction = 0f;
        }
        if (alphaFraction > 1f) {
            alphaFraction = 1f;
        }

        return Math.round(alphaFraction * 255);
    }

    private int mapFractionToAlpha(float alphaFraction, int startAlpha) {
        if (alphaFraction < 0f) {
            alphaFraction = 0f;
        }
        if (alphaFraction > 1f) {
            alphaFraction = 1f;
        }

        int alphaRange = 255 - startAlpha;
        int scaledToRange = Math.round(alphaFraction * alphaRange);
        return startAlpha + scaledToRange;
    }
}
