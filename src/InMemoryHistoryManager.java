import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Long, Node> historyMap;
    private HistoryLinkedList<Task> historyLinkedList;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
        historyLinkedList = new HistoryLinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getTaskId())) {
            Node<Task> deleteNode = historyMap.get(task.getTaskId());
            historyLinkedList.removeNode(deleteNode);
        }
        historyMap.put(task.getTaskId(), historyLinkedList.linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return historyLinkedList.getTasks();
    }

    @Override
    public void remove(long id) {
        if (historyMap.containsKey(id)) {
            historyLinkedList.removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }
}

