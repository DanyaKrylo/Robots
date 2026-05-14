package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.*;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener, LocaleChangeListener {
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super(LocaleManager.getInstance().getString("log.title"),
              true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        LocaleManager.getInstance().addListener(this);

        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();

        // Убираем дублирование, используя новый хелпер
        CloseDialogHelper.installCloseListener(this, "confirm.close", "confirm.title", this::unregisterListeners);
    }

    private void unregisterListeners() {
        m_logSource.unregisterListener(this);
        LocaleManager.getInstance().removeListener(this);
    }

    public void updateTitle() {
        setTitle(LocaleManager.getInstance().getString("log.title"));
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void onLocaleChanged() {
        updateTitle();
    }
}