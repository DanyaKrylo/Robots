package gui;

import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;          // <-- добавлен импорт
import javax.swing.*;

public class RobotsProgram {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Profile selectedProfile = null;
            Locale profileLocale = null;

            try {
                List<Path> profileFiles = ProfileManager.getAvailableProfiles();
                if (!profileFiles.isEmpty()) {
                    String[] options = new String[profileFiles.size() + 1];
                    Profile[] profiles = new Profile[profileFiles.size()];
                    for (int i = 0; i < profileFiles.size(); i++) {
                        try {
                            profiles[i] = ProfileManager.loadProfile(profileFiles.get(i));
                            Locale loc = profiles[i].getLocale();
                            String lang = loc.getDisplayLanguage(loc) + " (" + loc.toLanguageTag() + ")";
                            options[i] = profileFiles.get(i).getFileName().toString() + " — " + lang;
                        } catch (Exception ex) {
                            options[i] = profileFiles.get(i).getFileName().toString() + " (error)";
                        }
                    }
                    options[profileFiles.size()] = getMessage("profile.not_restore");

                    String message = getMessage("profile.question");
                    int choice = JOptionPane.showOptionDialog(
                            null,
                            message,
                            getMessage("profile.title"),
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[profileFiles.size()]
                    );

                    if (choice >= 0 && choice < profileFiles.size()) {
                        selectedProfile = profiles[choice];
                        profileLocale = selectedProfile.getLocale();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (profileLocale != null) {
                LocaleManager.getInstance().setLocale(profileLocale);
            }

            MainApplicationFrame frame = new MainApplicationFrame();
            if (selectedProfile != null) {
                frame.applyProfile(selectedProfile);
                frame.setExtendedState(selectedProfile.getMainFrameExtendedState());
                frame.setBounds(selectedProfile.getMainFrameBounds());
                frame.setVisible(true);
            } else {
                frame.pack();
                frame.setVisible(true);
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });
    }

    private static String getMessage(String key) {
        if ("profile.question".equals(key)) {
            return "Найдены сохранённые профили. Выберите один для восстановления:";
        } else if ("profile.title".equals(key)) {
            return "Восстановление профиля";
        } else if ("profile.not_restore".equals(key)) {
            return "Не восстанавливать";
        }
        return "";
    }
}