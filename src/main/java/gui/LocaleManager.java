package gui;

import java.util.*;

public class LocaleManager {
    private static LocaleManager instance;
    private Locale currentLocale;
    private ResourceBundle bundle;
    private final List<LocaleChangeListener> listeners = new ArrayList<>();

    private LocaleManager() {
        currentLocale = Locale.getDefault();
        loadBundle();
    }

    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        loadBundle();
        notifyListeners();
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    private void loadBundle() {
        bundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    public void addListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (LocaleChangeListener l : listeners) {
            l.onLocaleChanged();
        }
    }
}