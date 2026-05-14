package gui;

import java.awt.Rectangle;
import java.io.Serializable;

public class WindowState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private Rectangle bounds;
    private boolean isIcon;
    private boolean isMaximum;
    private boolean isVisible;

    // Пустой конструктор необходим для правильной работы Gson
    public WindowState() {}

    public WindowState(String id, Rectangle bounds, boolean isVisible, boolean isIcon, boolean isMaximum) {
        this.id = id;
        this.bounds = bounds;
        this.isVisible = isVisible;
        this.isIcon = isIcon;
        this.isMaximum = isMaximum;
    }

    public String getId() { return id; }
    public Rectangle getBounds() { return bounds; }
    public boolean isVisible() { return isVisible; }
    public boolean isIcon() { return isIcon; }
    public boolean isMaximum() { return isMaximum; }
}