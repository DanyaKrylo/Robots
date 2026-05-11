package gui;

import java.awt.Rectangle;

/**
 * Хранит состояние одного внутреннего окна (JInternalFrame).
 * Добавлено для сериализации/десериализации профиля.
 */
public class WindowState {
    private String type;          // тип окна: "log", "game"
    private Rectangle bounds;     // положение и размер
    private boolean icon;         // свёрнуто ли окно в иконку
    private boolean closed;       // закрыто ли окно

    public WindowState(String type, Rectangle bounds, boolean icon, boolean closed) {
        this.type = type;
        this.bounds = bounds;
        this.icon = icon;
        this.closed = closed;
    }

    public String getType() { return type; }
    public Rectangle getBounds() { return bounds; }
    public boolean isIcon() { return icon; }
    public boolean isClosed() { return closed; }
}