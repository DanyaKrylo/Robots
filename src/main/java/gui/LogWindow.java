package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
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
        LocaleManager.getInstance().addListener(this); // подписываемся на смену языка

        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();

        // Подтверждение закрытия окна (исправлено: используем CloseDialogHelper)
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (CloseDialogHelper.confirmClose(LogWindow.this, "confirm.close", "confirm.title")) {
                    unregisterListeners();
                    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    dispose(); // явно закрываем
                } else {
                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    // Отписываемся от лога и менеджера локализации
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