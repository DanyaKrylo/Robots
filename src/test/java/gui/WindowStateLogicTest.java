package gui;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

import static org.junit.jupiter.api.Assertions.*;

public class WindowStateLogicTest {

    /**
     * Надежный поиск компонента во всем дереве
     */
    private JInternalFrame findInternalFrame(Container container, Class<?> clazz) {
        if (container instanceof JDesktopPane) {
            for (JInternalFrame f : ((JDesktopPane) container).getAllFrames()) {
                if (clazz.isInstance(f)) return f;
            }
        }
        for (Component comp : container.getComponents()) {
            if (clazz.isInstance(comp)) {
                return (JInternalFrame) comp;
            }
            if (comp instanceof Container) {
                JInternalFrame found = findInternalFrame((Container) comp, clazz);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Test
    void testNormalizationAfterMaximizationAndIconification() throws PropertyVetoException {
        MainApplicationFrame mainFrame = new MainApplicationFrame();
        
        // Безопасный поиск игрового окна с помощью рекурсивного метода
        JInternalFrame gameWindow = findInternalFrame(mainFrame.getContentPane(), GameWindow.class);
        
        assertNotNull(gameWindow, "Не удалось найти GameWindow для теста");
        
        // 1. Устанавливаем нормальный размер
        Rectangle normalBounds = new Rectangle(50, 50, 200, 200);
        gameWindow.setBounds(normalBounds);
        
        // Оповещаем слушателя во фрейме, чтобы он запомнил размер
        if (gameWindow.getComponentListeners().length > 0) {
            gameWindow.getComponentListeners()[0].componentResized(null);
        }
        
        // 2. Максимизируем и сворачиваем
        gameWindow.setMaximum(true);
        gameWindow.setIcon(true);
        
        // 3. Проверяем, что в захваченном состоянии все верно
        Profile profile = mainFrame.captureProfile();
        WindowState gameStat = null;
        for (WindowState ws : profile.getInternalWindows()) {
            if (ws.getId().equals("game")) gameStat = ws;
        }

        assertNotNull(gameStat, "Статус окна game не найден в профиле");
        assertTrue(gameStat.isMaximum(), "Должен быть флаг максимизации");
        assertTrue(gameStat.isIcon(), "Должен быть флаг сворачивания");
        assertEquals(200, gameStat.getBounds().width, "Должна сохраниться ширина 200");

        // 4. Симуляция восстановления в новое окно
        JInternalFrame restoreFrame = new JInternalFrame();
        restoreFrame.setBounds(gameStat.getBounds());
        
        // Логика из MainApplicationFrame.applyProfile
        if (gameStat.isMaximum()) restoreFrame.setMaximum(true);
        if (gameStat.isIcon()) restoreFrame.setIcon(true);
        
        // Теперь "разворачиваем" обратно
        restoreFrame.setIcon(false);
        restoreFrame.setMaximum(false);
        
        // Должно вернуться к 200x200
        assertEquals(200, restoreFrame.getWidth(), "Окно не вернулось к нормальной ширине");
        assertEquals(200, restoreFrame.getHeight(), "Окно не вернулось к нормальной высоте");
    }
}