package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame implements LocaleChangeListener {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame() {
        // Регистрируемся в LocaleManager
        LocaleManager.getInstance().addListener(this);

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);
        setContentPane(desktopPane);

        // Создаём окна Лога и Игры с локализованными заголовками
        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = createGameWindow();
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        // Обработка закрытия главного окна с подтверждением
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private LogWindow createLogWindow() {
        LogWindow lw = new LogWindow(Logger.getDefaultLogSource());
        lw.setLocation(10, 10);
        lw.setSize(300, 800);
        setMinimumSize(lw.getSize());
        lw.pack();
        Logger.debug(getMessage("log.started"));
        return lw;
    }

    private GameWindow createGameWindow() {
        GameWindow gw = new GameWindow();
        gw.setSize(400, 400);
        return gw;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    // ---------- Меню ----------
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createLanguageMenu());   // новое меню выбора языка
        menuBar.add(createTestMenu());
        menuBar.add(createExitMenu());        // меню "Выйти"
        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu(getMessage("menu.lookandfeel"));
        menu.setMnemonic(KeyEvent.VK_V);

        JMenuItem systemItem = new JMenuItem(getMessage("menu.look.system"));
        systemItem.setMnemonic(KeyEvent.VK_S);
        systemItem.addActionListener(e -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        menu.add(systemItem);

        JMenuItem crossItem = new JMenuItem(getMessage("menu.look.crossplatform"));
        crossItem.setMnemonic(KeyEvent.VK_C);
        crossItem.addActionListener(e -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        menu.add(crossItem);

        return menu;
    }

    private JMenu createLanguageMenu() {
        JMenu menu = new JMenu(getMessage("menu.language"));
        menu.setMnemonic(KeyEvent.VK_L);

        JMenuItem ruItem = new JMenuItem("Русский");
        ruItem.addActionListener(e -> LocaleManager.getInstance().setLocale(new Locale("ru")));
        menu.add(ruItem);

        JMenuItem enItem = new JMenuItem("English");
        enItem.addActionListener(e -> LocaleManager.getInstance().setLocale(Locale.ENGLISH));
        menu.add(enItem);

        return menu;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu(getMessage("menu.tests"));
        menu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogItem = new JMenuItem(getMessage("menu.addlog"));
        addLogItem.setMnemonic(KeyEvent.VK_S);
        addLogItem.addActionListener(e -> Logger.debug(getMessage("log.newline")));
        menu.add(addLogItem);

        return menu;
    }

    private JMenu createExitMenu() {
        JMenu menu = new JMenu(getMessage("menu.file"));
        menu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitItem = new JMenuItem(getMessage("menu.exit"));
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(e -> confirmExit());
        menu.add(exitItem);

        return menu;
    }

    // ---------- Подтверждение выхода с использованием CloseDialogHelper ----------
    private void confirmExit() {
        if (CloseDialogHelper.confirmClose(this, "confirm.exit", "confirm.title")) {
            System.exit(0);
        }
    }

    // ---------- Локализованная строка ----------
    private String getMessage(String key) {
        return LocaleManager.getInstance().getString(key);
    }

    // ---------- Смена темы оформления ----------
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // ignore
        }
    }

    // ---------- Обновление интерфейса при смене языка ----------
    @Override
    public void onLocaleChanged() {
        // Пересоздаём меню с новыми строками
        setJMenuBar(generateMenuBar());
        // Обновляем заголовки внутренних окон
        if (logWindow != null) logWindow.updateTitle();
        if (gameWindow != null) gameWindow.updateTitle();
        // Перерисовываем
        revalidate();
        repaint();
    }
}