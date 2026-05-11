package gui;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class GameWindow extends JInternalFrame implements LocaleChangeListener {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super(LocaleManager.getInstance().getString("game.title"),
              true, true, true, true);
        m_visualizer = new GameVisualizer();
        LocaleManager.getInstance().addListener(this); // подписываемся на смену языка

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        // Подтверждение закрытия окна (исправлено: используем CloseDialogHelper)
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (CloseDialogHelper.confirmClose(GameWindow.this, "confirm.close", "confirm.title")) {
                    LocaleManager.getInstance().removeListener(GameWindow.this);
                    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    dispose(); // явно закрываем
                } else {
                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    public void updateTitle() {
        setTitle(LocaleManager.getInstance().getString("game.title"));
    }

    @Override
    public void onLocaleChanged() {
        updateTitle();
    }
}