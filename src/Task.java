import java.util.Objects;

public class Task {

    private long taskId;
    private String taskName;
    private Status taskStatus;
    private String taskDescription;

    public Task(String taskName,
                String taskDescription,
                Status taskStatus) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
    }
    public Task(long taskId,
                String taskName,
                String taskDescription,
                Status taskStatus) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId);
    }
               // Objects.equals(taskName, task.taskName) &&
               // taskStatus.equals(task.taskStatus) &&
               // Objects.equals(taskDescription, task.taskDescription);


    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
                //taskName, taskStatus, taskDescription);

}
