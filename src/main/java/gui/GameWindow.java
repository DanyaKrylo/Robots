package gui;

import java.awt.BorderLayout;
import javax.swing.*;

public class GameWindow extends JInternalFrame implements LocaleChangeListener {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super(LocaleManager.getInstance().getString("game.title"),
              true, true, true, true);
        m_visualizer = new GameVisualizer();
        LocaleManager.getInstance().addListener(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        // Убираем дублирование, используя новый хелпер
        CloseDialogHelper.installCloseListener(this, "confirm.close", "confirm.title", () -> {
            LocaleManager.getInstance().removeListener(this);
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