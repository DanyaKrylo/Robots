package gui;

import java.awt.Component;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * Вспомогательный класс для вызова диалога подтверждения закрытия.
 */
public class CloseDialogHelper {

    public static boolean confirmClose(Component parent, String messageKey, String titleKey) {
        String message = LocaleManager.getInstance().getString(messageKey);
        String title = LocaleManager.getInstance().getString(titleKey);
        int result = JOptionPane.showConfirmDialog( // Инкапсулировал в себе всю логику показа диалогового окна
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Универсальный метод установки обработчика закрытия внутреннего окна.
     */
    public static void installCloseListener(JInternalFrame frame, String messageKey, String titleKey, Runnable onConfirm) {
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE); 
        frame.addInternalFrameListener(new InternalFrameAdapter() { // Обработка событий закрытия внутреннего фрейма
        	@Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (confirmClose(frame, messageKey, titleKey)) {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                    frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
                    frame.dispose();
                }
            }
        });
    }
}