package gui;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {
    @Test
    void testJsonSerializationCompatibility() {
        Gson gson = new Gson();
        Rectangle rect = new Rectangle(10, 20, 100, 200);
        WindowState originalState = new WindowState("test-id", rect, true, false, true);
        
        // Сериализация
        String json = gson.toJson(originalState);
        
        // Десериализация
        WindowState restoredState = gson.fromJson(json, WindowState.class);
        
        assertEquals(originalState.getId(), restoredState.getId());
        assertEquals(originalState.getBounds(), restoredState.getBounds());
        assertEquals(originalState.isMaximum(), restoredState.isMaximum());
        assertEquals(originalState.isIcon(), restoredState.isIcon());
    }
}