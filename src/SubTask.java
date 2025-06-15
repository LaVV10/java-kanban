import java.util.Objects;

public class SubTask extends Task{

    private long epicId;

    public SubTask(String taskName,
                   String taskDescription,
                   long epicId) {
        super(taskName, taskDescription);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
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
        return this.getEpicId() == that.getEpicId() &&
                Objects.equals(this.getTaskId(), that.getTaskId()) &&
                this.getTaskName().equals(that.getTaskName()) &&
                this.getTaskDescription().equals(that.getTaskDescription()) &&
                this.getTaskStatus().equals(that.getTaskStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskName(),
                this.getTaskDescription(),
                this.getTaskId(),
                this.getTaskStatus());
    }
}
