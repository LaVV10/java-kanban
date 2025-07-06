import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(task); // добавляем задачу в конец истории
        if (history.size() > 10) {
            history.remove(0); // удаляем самую старую задачу, если список превысил 10 элементов
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
