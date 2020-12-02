import java.util.List;

public class Statistics {

    private List<WorkLog> workLogs;
    private List<Task> taskList;

    public Statistics(List<WorkLog> workLogArrayList, List<Task> taskList) {
        this.workLogs = workLogArrayList;
        this.taskList = taskList;
    }
}