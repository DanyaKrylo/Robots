package gui;

import java.awt.Rectangle;
import java.util.List;
import java.util.Locale;

/**
 * Профиль приложения: локаль, состояние главного окна и список внутренних окон.
 * Добавлен для сохранения/восстановления.
 */
public class Profile {
    private String localeTag;                // языковой тег (ru, en)
    private Rectangle mainFrameBounds;       // координаты и размер главного окна
    private int mainFrameExtendedState;      // JFrame.EXTENDED_STATE (NORMAL, MAXIMIZED_BOTH и т.д.)
    private List<WindowState> internalWindows; // состояния внутренних окон

    // Пустой конструктор нужен для Gson
    public Profile() {}

    public Profile(Locale locale, Rectangle mainBounds, int mainState, List<WindowState> windows) {
        this.localeTag = locale.toLanguageTag();
        this.mainFrameBounds = mainBounds;
        this.mainFrameExtendedState = mainState;
        this.internalWindows = windows;
    }

    public Locale getLocale() { return Locale.forLanguageTag(localeTag); }
    public Rectangle getMainFrameBounds() { return mainFrameBounds; }
    public int getMainFrameExtendedState() { return mainFrameExtendedState; }
    public List<WindowState> getInternalWindows() { return internalWindows; }
}