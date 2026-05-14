package gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameVisualizerTest {

    @Test
    public void testMoveRobotForward() {
        GameVisualizer vis = new GameVisualizer();
        // Устанавливаем робота на x=100, y=100, направление = 0 (смотрит строго вправо)
        vis.setRobotPosition(100.0, 100.0, 0.0);
        
        // Двигаем робота (скорость 0.1, угловая скорость 0, длительность 10)
        vis.moveRobot(0.1, 0.0, 10.0);
        
        // Математика: 
        // newX = 100 + (0.1 * 10 * cos(0)) = 100 + 1 = 101.0
        // newY = 100 - (0.1 * 10 * sin(0)) = 100 - 0 = 100.0
        
        assertEquals(101.0, vis.getRobotPositionX(), 0.001, "Робот должен был сместиться по оси X на 1.0");
        assertEquals(100.0, vis.getRobotPositionY(), 0.001, "Позиция по оси Y не должна была измениться");
    }
}