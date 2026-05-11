package gui;

/**
 * Интерфейс слушателя изменения локали.
 * Вынесен в отдельный файл, чтобы быть доступным всем классам пакета.
 */
public interface LocaleChangeListener {
    void onLocaleChanged();
}