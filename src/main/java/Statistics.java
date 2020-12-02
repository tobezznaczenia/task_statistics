import java.util.*;

public class Statistics {

    private List<WorkLog> workLogs;
    private List<Task> taskList;

    public Statistics(List<WorkLog> workLogArrayList, List<Task> taskList) {
        this.workLogs = workLogArrayList;
        this.taskList = taskList;
    }

    public Map<Long, Long> getLoggedTimeForEpics(){
        Map<Long, Long> epicTasks = new HashMap<Long, Long>();
        taskList.stream().filter(t -> t.parent == -1).forEach(t->epicTasks.put(t.id, (long) 0));
        for (Map.Entry<Long,Long> entry : epicTasks.entrySet()){
            long fullTime = workLogs.stream().filter(w -> w.taskId == entry.getKey()).mapToLong(w -> w.timeLogged).sum();
            fullTime = getLoggedTimeOfSubTasks(entry.getKey(), fullTime);
            epicTasks.put(entry.getKey(), fullTime);
        }
        return  new TreeMap<>(epicTasks);
    }

    public long getLoggedTimeOfSubTasks(Long id, long fullTimeOfSubTasks){
        List<Long> subTasksIds = new ArrayList<Long>();
        taskList.stream().filter(t -> t.parent == id).forEach(t->subTasksIds.add(t.id));
        for(Long subtask : subTasksIds){
            fullTimeOfSubTasks += workLogs.stream().filter(w -> w.taskId == subtask).mapToLong(w -> w.timeLogged).sum();
        }
        for(Long subtask : subTasksIds){
            fullTimeOfSubTasks = getLoggedTimeOfSubTasks(subtask, fullTimeOfSubTasks);
        }
        return fullTimeOfSubTasks;
    }
}