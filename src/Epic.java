import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks;

    public Epic(String taskName,
                String taskDescription) {
        super(taskName, taskDescription, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTask(SubTask subTaskId) {
        subTasks.add(subTaskId); // Добавляем подзадачу
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

    private void checkEpicStatus() {
        // текущий статус эпика
        Status epicStatus = getTaskStatus();
        if (getSubTasks().isEmpty()) {
            epicStatus = Status.NEW; // если нет подзадач, статус эпика NEW
        } else {
            boolean allNew = true;  // признак того, что все подзадачи NEW
            boolean allDone = true; // признак того, что все подзадачи DONE

            for (SubTask subTask : getSubTasks()) {
                Status statusSubtask = subTask.getTaskStatus();
                if (statusSubtask != Status.NEW) {
                    allNew = false; // если хотя бы одна подзадача не NEW, снимаем признак
                }
                if (statusSubtask != Status.DONE) {
                    allDone = false; // если хотя бы одна подзадача не DONE, снимаем признак
                }
            }
            if (allNew) {
                epicStatus = Status.NEW;  // если все подзадачи NEW, эпик NEW
            } else if (allDone) {
                epicStatus = Status.DONE; // если все подзадачи DONE, эпик DONE
            } else {
                epicStatus = Status.IN_PROGRESS; // в противном случае эпик IN_PROGRESS
            }
        }
        setTaskStatus(epicStatus);
    }

    @Override
    public String toString() {
        ArrayList<Long> subTaskId = new ArrayList<>();
        for (SubTask subTask : subTasks) {
            subTaskId.add(subTask.getTaskId());
        }
        String result = "Epic{" +
                "taskName='" + getTaskName() + '\'' +
                ", taskDescription='" + getTaskDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", taskStatus='" + getTaskStatus() + '\'' +
                ", subTaskId=" + subTaskId +
                '}';
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(this.getTaskId(), task.getTaskId()) &&
                this.getTaskName().equals(task.getTaskName()) &&
                this.getTaskDescription().equals(task.getTaskDescription()) &&
                this.getTaskStatus().equals(task.getTaskStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskName(),
                this.getTaskDescription(),
                this.getTaskId(),
                this.getTaskStatus());
    }
}
