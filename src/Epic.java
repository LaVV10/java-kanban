import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks;

    public Epic(String taskName,
                String taskDescription) {
        super(taskName, taskDescription, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public Epic(long taskId,
                String taskName,
                String taskDescription) {
        super(taskId, taskName, taskDescription, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getTaskId() == this.getTaskId()) {
            throw new IllegalArgumentException("Эпик не может быть подзадачей самого себя");
        }
        subTasks.add(subTask); // Добавляем подзадачу
        checkEpicStatus();
    }

    public void deleteSubTask(SubTask subTaskToDelete) {
        for (int i = subTasks.size() - 1; i >= 0; i--) {
            if (subTasks.get(i).getTaskId() == subTaskToDelete.getTaskId()) {
                subTasks.remove(i);
            }
        }
        checkEpicStatus();
    }

    // Очищаем список подзадач
    public void clearSubTasks() {
        subTasks.clear();
        checkEpicStatus();
    }

    private void checkEpicStatus() {
        // текущий статус эпика
        if (subTasks.isEmpty()) {
            setTaskStatus(Status.NEW); // Если нет подзадач, статус NEW
        } else {
            boolean allNew = true; // Признак того, что все подзадачи NEW
            boolean allDone = true; // Признак того, что все подзадачи DONE

            for (SubTask subTask : subTasks) {
                if (subTask.getTaskStatus() != Status.NEW) {
                    allNew = false; // Если хотя бы одна подзадача не NEW, снимаем признак
                }
                if (subTask.getTaskStatus() != Status.DONE) {
                    allDone = false; // Если хотя бы одна подзадача не DONE, снимаем признак
                }
            }

            if (allNew) {
                setTaskStatus(Status.NEW); // Если все подзадачи NEW, эпик NEW
            } else if (allDone) {
                setTaskStatus(Status.DONE); // Если все подзадачи DONE, эпик DONE
            } else {
                setTaskStatus(Status.IN_PROGRESS); // Во всех остальных случаях эпик IN_PROGRESS
            }
        }
    }

    @Override
    public String toString() {
        ArrayList<Long> subTaskId = new ArrayList<>();
        for (SubTask subTask : subTasks) {
            subTaskId.add(subTask.getTaskId());
        }
        String result = "Epic {" +
                "taskName = " + getTaskName() +
                ", taskDescription = " + getTaskDescription() +
                ", taskId = " + getTaskId() +
                ", taskStatus = " + getTaskStatus() +
                ", subTaskId = " + subTaskId +
                '}';
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(this.getTaskId(), task.getTaskId());
                /*this.getTaskName().equals(task.getTaskName()) &&
                this.getTaskDescription().equals(task.getTaskDescription()) &&
                this.getTaskStatus().equals(task.getTaskStatus());*/
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskId());
                /*this.getTaskName(),
                this.getTaskDescription(),
                this.getTaskStatus());*/
    }
}
