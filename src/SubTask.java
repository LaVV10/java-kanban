import java.util.Objects;

public class SubTask extends Task {

    private long epicId;

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
        return "SubTask{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskId=" + this.getTaskId() +
                ", taskStatus='" + this.getTaskStatus() + '\'' +
                ", epicId=" + this.getEpicId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask that = (SubTask) o;
        return  this.getTaskId() == that.getTaskId() &&
                this.getTaskName().equals(that.getTaskName()) &&
                this.getTaskDescription().equals(that.getTaskDescription()) &&
                this.getTaskStatus().equals(that.getTaskStatus()) &&
                this.getEpicId() == that.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskName(),
                this.getTaskDescription(),
                this.getTaskId(),
                epicId,
                this.getTaskStatus());
    }
}
