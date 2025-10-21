import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private long epicId;

    public SubTask(String taskName,
                   String taskDescription,
                   Status taskStatus,
                   long epicId,
                   LocalDateTime startTime,
                   Duration duration) {
        super(taskName, taskDescription, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String taskName,
                   String taskDescription,
                   Status taskStatus,
                   long epicId) {
        super(taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask {" +
                "taskName = " + this.getTaskName() +
                ", taskDescription = " + this.getTaskDescription() +
                ", taskId = " + this.getTaskId() +
                ", taskStatus = " + this.getTaskStatus() +
                ", epicId = " + this.getEpicId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask that = (SubTask) o;
        return this.getTaskId() == that.getTaskId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskId(),
                epicId);
    }
}
