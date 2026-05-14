package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame implements LocaleChangeListener {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;
    
    private final Map<String, JInternalFrame> windowMap = new HashMap<>();
    private final Map<String, Rectangle> normalBoundsMap = new HashMap<>();

    public MainApplicationFrame() {
        LocaleManager.getInstance().addListener(this);
        
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        initWindows();
        setJMenuBar(generateMenuBar());
        setTitle(getString("main.title")); // Установка заголовка при старте

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
        	@Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void initWindows() {
        logWindow = new LogWindow(Logger.getDefaultLogSource());
        addWindow(logWindow, "log", new Rectangle(10, 10, 300, 600));

        gameWindow = new GameWindow();
        addWindow(gameWindow, "game", new Rectangle(320, 10, 400, 400));
    }

    private void addWindow(JInternalFrame frame, String id, Rectangle defaultBounds) {
        frame.setBounds(defaultBounds);
        normalBoundsMap.put(id, defaultBounds);
        windowMap.put(id, frame);

        frame.addComponentListener(new ComponentAdapter() { // Был добавлен компонент Listener, он отслеживает изменения
        	@Override // размеров окна и сохраняет их normalBoundsMap только тогда когда оно не максимизированно и не свернуто
            public void componentResized(ComponentEvent e) {
                if (!frame.isMaximum() && !frame.isIcon()) {
                    normalBoundsMap.put(id, frame.getBounds());
                }
            }
        	@Override
            public void componentMoved(ComponentEvent e) {
                if (!frame.isMaximum() && !frame.isIcon()) {
                    normalBoundsMap.put(id, frame.getBounds());
                }
            }
        });

        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public Profile captureProfile() {
        List<WindowState> states = new ArrayList<>();
        for (Map.Entry<String, JInternalFrame> entry : windowMap.entrySet()) {
            String id = entry.getKey();
            JInternalFrame frame = entry.getValue();
            
            Rectangle bounds = normalBoundsMap.get(id); // Сохраняются именно эти исходные квадратные координаты из мапы, а не искаженные полноэкранные
            if (bounds == null) bounds = frame.getBounds();
            
            states.add(new WindowState(
                id, 
                bounds, 
                frame.isVisible(), 
                frame.isIcon(), 
                frame.isMaximum()
            ));
        }
        
        return new Profile(
            LocaleManager.getInstance().getCurrentLocale(), 
            this.getBounds(), 
            this.getExtendedState(), 
            states
        );
    }

    public void applyProfile(Profile profile) { 
        if (profile == null) return;

        List<WindowState> states = profile.getInternalWindows();
        if (states == null) return;

        for (WindowState ws : states) {
            JInternalFrame frame = windowMap.get(ws.getId());
            if (frame != null) {
                frame.setBounds(ws.getBounds());
                normalBoundsMap.put(ws.getId(), ws.getBounds());
                frame.setVisible(ws.isVisible());

                SwingUtilities.invokeLater(() -> { // Был жестко задан порядок применения состояний
                    try {
                        // Сначала применяем развернутое состояние
                        if (ws.isMaximum()) {
                            frame.setMaximum(true);
                        } else {
                            frame.setMaximum(false);
                        }
                     // Затем применяем состояние иконки (свернутое)
                        // Благодаря такому порядку, если окно свернули будучи развернутым,
                        // оно восстановится свернутым, а при разворачивании снова займет весь экран.
                        if (ws.isIcon()) {
                            frame.setIcon(true);
                        } else {
                            frame.setIcon(false);
                        }
                    } catch (PropertyVetoException e) {
                        Logger.error("Failed to set window state for " + ws.getId());
                    }
                });
            }
        }
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createLanguageMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu(getString("menu.file"));
        JMenuItem exitItem = new JMenuItem(getString("menu.exit"));
        exitItem.addActionListener(e -> confirmExit());
        menu.add(exitItem);
        return menu;
    }

    private JMenu createLanguageMenu() {
        JMenu menu = new JMenu(getString("menu.language"));
        JMenuItem ruItem = new JMenuItem("Русский");
        ruItem.addActionListener(e -> LocaleManager.getInstance().setLocale(new Locale("ru")));
        JMenuItem enItem = new JMenuItem("English");
        enItem.addActionListener(e -> LocaleManager.getInstance().setLocale(Locale.ENGLISH));
        menu.add(ruItem);
        menu.add(enItem);
        return menu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu(getString("menu.lookandfeel"));
        JMenuItem systemItem = new JMenuItem(getString("menu.look.system"));
        systemItem.addActionListener(e -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        JMenuItem crossItem = new JMenuItem(getString("menu.look.crossplatform"));
        crossItem.addActionListener(e -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        menu.add(systemItem);
        menu.add(crossItem);
        return menu;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu(getString("menu.tests"));
        JMenuItem logItem = new JMenuItem(getString("menu.addlog"));
        logItem.addActionListener(e -> Logger.debug(getString("log.newline")));
        menu.add(logItem);
        return menu;
    }

    private void confirmExit() {
        if (CloseDialogHelper.confirmClose(this, "confirm.exit", "confirm.title")) {
            ProfileManager.saveProfile(captureProfile());
            System.exit(0);
        }
    }

    private String getString(String key) {
        return LocaleManager.getInstance().getString(key);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            Logger.error("Look and Feel error");
        }
    }

    @Override
    public void onLocaleChanged() {
        setJMenuBar(generateMenuBar());
        setTitle(getString("main.title"));
        if (logWindow != null) logWindow.updateTitle();
        if (gameWindow != null) gameWindow.updateTitle();
        revalidate();
        repaint();
    }
}