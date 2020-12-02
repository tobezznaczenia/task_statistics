import com.google.gson.annotations.SerializedName;

public class WorkLog {
    String author;
    Long timeLogged;
    Long date;
    @SerializedName("taskID")
    Long taskId;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getTimeLogged() {
        return timeLogged;
    }

    public void setTimeLogged(Long timeLogged) {
        this.timeLogged = timeLogged;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

}
