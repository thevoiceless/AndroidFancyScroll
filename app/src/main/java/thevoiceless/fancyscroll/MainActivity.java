package thevoiceless.fancyscroll;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.WindowCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
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
                // Use the opacity at the time the drawer opens as the minumum to avoid flashing
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
        // Save the scroll position
        outState.putInt(KEY_SCROLL_POS, scrollView.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Get the saved scroll position
        final int position = savedInstanceState.getInt(KEY_SCROLL_POS);

        // Credit goes to http://stackoverflow.com/a/15301092/1693087
        // We can't call scrollTo() until the ScrollView has finished setting up its layout
        // Supposedly any Runnables posted to a View will be executed only once the View is ready
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, position);

                // We also need to set the correct transparency for the action bar if the nav drawer
                // was open before the rotation occurred
                if (drawer.isDrawerOpen(drawerList)) {
                    actionBarBG.setAlpha(mapFractionToAlpha(1f, actionBarOpacity));
                }
            }
        });

        // I'll keep the alternative implementation here just in case the post() strategy turns out
        // to be incorrect
        /*
        if (scrollView.getViewTreeObserver().isAlive()) {
            scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    scrollView.scrollTo(0, position);

                    if (scrollView.getViewTreeObserver().isAlive()) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            // The method name changed in Jelly Bean
                            scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
        }
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * This is the callback used by ObservableScrollview that allows us to keep track of how far
     * (in pixels) the user has scrolled
     */
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
        // Map the visible percentage of the ImageView to the fractional opacity of the action bar
        float percentHidden = (scrollView.getScrollY() / (float) cover.getHeight());
        setActionBarOpacity(mapFractionToAlpha(percentHidden));

        // Set the top padding of the ImageView to 1/2 of the scrolled distance to get the "parallax"
        // effect, making the image look like it's scrolling slower than everything else
        int top = scrollView.getScrollY() / 2;
        cover.setPadding(0, top, 0, 0);
    }

    /*
     * Because we adjust the top padding of the image, we need to set a different ScaleType based on
     * orientation
     * I'm not sure how this will affect behavior in all situations; the image I used has a greater
     * width than height, but the image is not wide enough to fill my Nexus 4's screen without scaling
     */
    private void setImageViewScaling() {
        //
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180) {
            // Portrait, use "center" so that the image doesn't shrink as the padding is increased
            cover.setScaleType(ImageView.ScaleType.CENTER);
        } else {
            // Landscape, use "centerCrop" to fill the width of the screen
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
