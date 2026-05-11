package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame implements LocaleChangeListener {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;

    // Карта для быстрого доступа к окнам по их типу
    private final Map<String, JInternalFrame> windowMap = new HashMap<>();

    public MainApplicationFrame() {
        LocaleManager.getInstance().addListener(this);

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);
        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);
        windowMap.put("log", logWindow);

        gameWindow = createGameWindow();
        addWindow(gameWindow);
        windowMap.put("game", gameWindow);

        setJMenuBar(generateMenuBar());

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
        menuBar.add(createLanguageMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createExitMenu());
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

    // ---------- Профилирование ----------
    /**
     * Собирает текущее состояние приложения.
     */
    public Profile captureProfile() {
        Locale locale = LocaleManager.getInstance().getCurrentLocale();
        Rectangle mainBounds = getBounds();
        int mainState = getExtendedState();

        java.util.List<WindowState> windows = new ArrayList<>();
        for (JInternalFrame frame : windowMap.values()) {
            if (frame == null) continue;
            String type = frame instanceof LogWindow ? "log" : "game";
            windows.add(new WindowState(
                    type,
                    frame.getBounds(),
                    frame.isIcon(),
                    frame.isClosed()
            ));
        }
        return new Profile(locale, mainBounds, mainState, windows);
    }

    /**
     * Восстанавливает состояние приложения из профиля.
     */
    public void applyProfile(Profile profile) {
        // Главное окно
        Rectangle mainBounds = profile.getMainFrameBounds();
        if (mainBounds != null) {
            setBounds(mainBounds);
        }
        setExtendedState(profile.getMainFrameExtendedState());

        // Внутренние окна
        java.util.List<WindowState> winStates = profile.getInternalWindows();
        if (winStates != null) {
            for (WindowState ws : winStates) {
                JInternalFrame frame = windowMap.get(ws.getType());
                if (frame != null) {
                    frame.setBounds(ws.getBounds());
                    try {
                        frame.setIcon(ws.isIcon());
                    } catch (Exception e) { /* игнорируем, если не поддерживается */ }
                    if (ws.isClosed()) {
                        frame.dispose();
                        windowMap.remove(ws.getType());
                    }
                }
            }
        }
        revalidate();
        repaint();
    }

    // ---------- Выход с сохранением ----------
    private void confirmExit() {
        if (CloseDialogHelper.confirmClose(this, "confirm.exit", "confirm.title")) {
            ProfileManager.saveProfile(captureProfile()); // сохранение профиля
            System.exit(0);
        }
    }

    // ---------- Вспомогательные методы ----------
    private String getMessage(String key) {
        return LocaleManager.getInstance().getString(key);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void onLocaleChanged() {
        setJMenuBar(generateMenuBar());
        if (logWindow != null) logWindow.updateTitle();
        if (gameWindow != null) gameWindow.updateTitle();
        revalidate();
        repaint();
    }
}