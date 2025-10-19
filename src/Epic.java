import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<SubTask> subTasks = new ArrayList<>();

    public Epic(String taskName,
                String taskDescription) {
        super(taskName, taskDescription, Status.NEW, null, null);
    }

    private void recalculateEpicData() {
        if (subTasks.isEmpty()) {
            setStartTime(null); // Если нет подзадач, то нет и времени начала
            setDuration(Duration.ZERO); // Общая продолжительность — 0
            return;
        }

        // Находим минимальную дату начала и сумму продолжительностей
        LocalDateTime minStartTime = null;
        Duration totalDuration = Duration.ZERO;
        for (SubTask subTask : subTasks) {
            if (minStartTime == null || subTask.getStartTime().isBefore(minStartTime)) {
                minStartTime = subTask.getStartTime();
            }
            totalDuration = totalDuration.plus(subTask.getDuration());
        }

        setStartTime(minStartTime); // Самое раннее время начала
        setDuration(totalDuration); // Сумма продолжительностей подзадач
    }

    // Метод для получения времени завершения эпика
    public LocalDateTime getEndTime() {
        if (getStartTime() == null || getDuration() == null) {
            return null;
        }
        return getStartTime().plus(getDuration());
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getTaskId() == this.getTaskId()) {
            throw new IllegalArgumentException("Эпик не может быть подзадачей самого себя");
        }
        subTasks.add(subTask); // Добавляем подзадачу
        checkEpicStatus();
        recalculateEpicData();
    }

    public void deleteSubTask(SubTask subTask) {
        subTasks.removeIf(st -> st.getTaskId() == subTask.getTaskId()); // Быстрое удаление
        recalculateEpicData(); // Пересчитываем данные эпика
    }

    // Очищаем список подзадач
    public void clearSubTasks() {
        subTasks.clear();
        checkEpicStatus();
        recalculateEpicData();
    }

    private void checkEpicStatus() {
        if (subTasks.isEmpty()) {
            setTaskStatus(Status.NEW);
        } else {
            boolean allNew = subTasks.stream()
                    .allMatch(subTask -> subTask.getTaskStatus() == Status.NEW);
            boolean allDone = subTasks.stream()
                    .allMatch(subTask -> subTask.getTaskStatus() == Status.DONE);

            if (allNew) {
                setTaskStatus(Status.NEW);
            } else if (allDone) {
                setTaskStatus(Status.DONE);
            } else {
                setTaskStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public String toString() {
        List<Long> subTaskId = new ArrayList<>();
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
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskId());
    }
}
