import java.util.Objects;

public class SubTask extends Task {

    private Epic epic;


    public SubTask(String taskName,
                   String taskDescription,
                   Status taskStatus,
                   Epic epic) {
        super(taskName, taskDescription, taskStatus);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskId=" + this.getTaskId() +
                ", taskStatus='" + this.getTaskStatus() + '\'' +
                ", epicId=" + epic.getTaskId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask that = (SubTask) o;
        return Objects.equals(this.getTaskId(), that.getTaskId()) &&
                this.getTaskName().equals(that.getTaskName()) &&
                this.getTaskDescription().equals(that.getTaskDescription()) &&
                this.getTaskStatus().equals(that.getTaskStatus()) &&
                this.getEpic().equals(that.getEpic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskName(),
                this.getTaskDescription(),
                this.getTaskId(),
                epic.getTaskId(),
                this.getTaskStatus());
    }
}
