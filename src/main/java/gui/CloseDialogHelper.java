package gui;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Вспомогательный класс для вызова диалога подтверждения закрытия.
 * Устраняет дублирование кода в MainApplicationFrame, LogWindow и GameWindow.
 */
public class CloseDialogHelper {

    /**
     * Показывает диалог подтверждения и возвращает true, если пользователь согласился.
     *
     * @param parent     родительский компонент для диалога
     * @param messageKey ключ сообщения в ResourceBundle
     * @param titleKey   ключ заголовка в ResourceBundle
     * @return true, если выбрано YES
     */
    public static boolean confirmClose(Component parent, String messageKey, String titleKey) {
        String message = LocaleManager.getInstance().getString(messageKey);
        String title = LocaleManager.getInstance().getString(titleKey);
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
}