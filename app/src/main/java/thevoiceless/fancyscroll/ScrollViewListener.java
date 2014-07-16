package thevoiceless.fancyscroll;

/**
 * Credit goes to http://stackoverflow.com/a/3952629/1693087
 */
public interface ScrollViewListener {

    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY);
}
