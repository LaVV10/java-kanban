import java.util.ArrayList;
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
        historyMap.put(task.getTaskId(),historyLinkedList.linkLast(task));
    }

    @Override
    public List<Task> getHistory(){
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
class HistoryLinkedList<T> {

    private Node<T> head;  // указатель на первый элемент связанного списка
    private Node<T> tail;  // указатель на последний элемент связанного списка

    private int size = 0;

    public Node<T> linkLast(T element) {
        Node<T> newNode = new Node<>(tail, element, null);
        if (tail != null) {
            tail.setNext(newNode);
        }
        tail = newNode;
        if (head == null) {
            head = newNode;
        }
        size++;
        return newNode;
    }

    public void removeNode(Node<T> element) {
        if (element == head) {
            head = element.getNext();
            if (head != null) {
                head.setPrev(null);
            }
        } else if (element == tail) {
            tail = element.getPrev();
            if (tail != null) {
                tail.setNext(null);
            }
        } else {
            Node<T> prev = element.getPrev();
            Node<T> next = element.getNext();
            prev.setNext(next);
            next.setPrev(prev);
        }
        size--;
    }

    public List<Task> getTasks() {
        List<Task> historyTasks = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            historyTasks.add((Task) current.getData());
            current = current.getNext();
        }
        return historyTasks;
    }
}

