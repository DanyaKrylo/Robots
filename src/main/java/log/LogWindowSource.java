package log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/**
 * Исправления:
 * 1. Заменён ArrayList на Deque (LinkedList) для эффективной фиксации размера очереди.
 * 2. При добавлении (append) сообщения сверх m_iQueueLength удаляем самое старое.
 * 3. Добавлен метод unregisterListener, слушатели теперь могут отписываться (устраняет утечку при закрытии окна лога).
 */
public class LogWindowSource {
    private final int m_iQueueLength;
    private final Deque<LogEntry> m_messages;
    private final List<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<>();
        m_listeners = new ArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        synchronized (m_messages) { // синхронизация по коллекции сообщений
            m_messages.addLast(entry);
            if (m_messages.size() > m_iQueueLength) {
                m_messages.removeFirst(); // удаляем самое старое
            }
        }
        LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners == null) {
            synchronized (m_listeners) {
                if (m_activeListeners == null) {
                    activeListeners = m_listeners.toArray(new LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        synchronized (m_messages) {
            return m_messages.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        synchronized (m_messages) {
            if (startFrom < 0 || startFrom >= m_messages.size()) {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, m_messages.size());
            // Преобразуем в список для subList, но проще создать копию части
            return new ArrayList<>(m_messages).subList(startFrom, indexTo);
        }
    }

    public Iterable<LogEntry> all() {
        synchronized (m_messages) {
            return new ArrayList<>(m_messages);
        }
    }
}