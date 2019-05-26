package conversion7.game.ui.list_menu;

public class MenuItem {
    public final String text;
    public final Runnable runnable;
    public boolean hideMenuOnClick

    public MenuItem(String text, Runnable runnable) {
        this(text, runnable, true)
    }

    public MenuItem(String text, Runnable runnable, hideMenuOnClick) {
        this.text = text;
        this.runnable = runnable;
        this.hideMenuOnClick = hideMenuOnClick;
    }
}
