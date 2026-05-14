package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

import static org.junit.jupiter.api.Assertions.*;

public class ProfilePersistenceTest {
    private MainApplicationFrame frame;

    @BeforeEach // Создает новый экземпляр главного окна перед каждым тестом
    void setUp() {
        // Создаем фрейм
        frame = new MainApplicationFrame();
    }

    /**
     * Универсальный вспомогательный метод для поиска окна в контейнере.
     * Правильно обрабатывает ситуацию, когда ContentPane сам является JDesktopPane.
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
    void testCaptureAndApplyProfile() throws PropertyVetoException {
        // Ищем игровое окно через исправленный вспомогательный метод
        JInternalFrame gameWindow = findInternalFrame(frame.getContentPane(), GameWindow.class);

        assertNotNull(gameWindow, "GameWindow должен существовать в MainApplicationFrame");

        // Устанавливаем координаты и максимизируем
        Rectangle testBounds = new Rectangle(100, 100, 250, 250);
        gameWindow.setBounds(testBounds);
        
        // Чтобы MainApplicationFrame запомнил эти границы как "нормальные"
        if (gameWindow.getComponentListeners().length > 0) {
            gameWindow.getComponentListeners()[0].componentResized(null);
        }
        
        gameWindow.setMaximum(true);

        // Действие: захватываем профиль
        Profile capturedProfile = frame.captureProfile();

        // Проверка: данные в профиле
        boolean foundGame = false;
        for (WindowState ws : capturedProfile.getInternalWindows()) {
            if (ws.getId().equals("game")) {
                foundGame = true;
                assertTrue(ws.isMaximum(), "В профиле должна быть пометка о максимизации");
                assertEquals(testBounds.width, ws.getBounds().width, "Должна быть сохранена нормальная ширина");
            }
        }
        assertTrue(foundGame, "Окно 'game' должно быть в профиле");
    }
}