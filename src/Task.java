import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task implements Comparable<Task> {

    private long taskId;
    private String taskName;
    private Status taskStatus;
    private String taskDescription;
    private static long id;
    protected LocalDateTime startTime; // Время начала задачи
    protected Duration duration;

    public Task(String taskName,
                String taskDescription,
                Status taskStatus,
                LocalDateTime startTime,
                Duration duration) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName,
                String taskDescription,
                Status taskStatus) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
    }

    @Override
    public int compareTo(Task other) {
        if (this.startTime == null || other.startTime == null) {
            return Boolean.compare(this.startTime != null, other.startTime != null);
        }
        return this.startTime.compareTo(other.startTime);
    }

    public static boolean isOverlapping(Task a, Task b) {
        if (a.getStartTime() == null || a.getDuration() == null ||
                b.getStartTime() == null || b.getDuration() == null) {
            return false;
        }

        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd = a.getEndTime();

        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd = b.getEndTime();

        // Проверка наложение отрезков [aStart, aEnd) и [bStart, bEnd)
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public static long getNewId() {
        return ++id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public String toString() {
        return "Task {" +
                "taskName = " + taskName +
                ", taskDescription = " + taskDescription +
                ", taskStatus = " + taskStatus +
                ", taskId = " + taskId +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
