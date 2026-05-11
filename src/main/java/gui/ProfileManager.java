package gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Отвечает за сохранение и загрузку профилей в папку "profiles".
 * Добавлен для поддержки нескольких профилей.
 */
public class ProfileManager {
    private static final String PROFILE_DIR = "profiles";
    private static final String PROFILE_EXT = ".json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static Path getProfileDir() {
        return Paths.get(System.getProperty("user.dir"), PROFILE_DIR);
    }

    /**
     * Сохраняет профиль в новый файл с именем по текущей дате/времени.
     */
    public static void saveProfile(Profile profile) {
        try {
            Files.createDirectories(getProfileDir());
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "profile_" + timestamp + PROFILE_EXT;
            Path file = getProfileDir().resolve(filename);
            try (Writer writer = Files.newBufferedWriter(file)) {
                gson.toJson(profile, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружает профиль из указанного файла.
     */
    public static Profile loadProfile(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            return gson.fromJson(reader, Profile.class);
        }
    }

    /**
     * Возвращает список доступных профилей (файлов), отсортированный по дате — новые раньше.
     */
    public static List<Path> getAvailableProfiles() throws IOException {
        Path dir = getProfileDir();
        if (!Files.exists(dir)) {
            return Collections.emptyList();
        }
        return Files.list(dir)
                .filter(p -> p.toString().endsWith(PROFILE_EXT))
                .sorted((a, b) -> Long.compare(b.toFile().lastModified(), a.toFile().lastModified()))
                .collect(Collectors.toList());
    }
}