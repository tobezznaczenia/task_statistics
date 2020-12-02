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

    public Map <String, Long> getLoggedTimeForProjects(){
        Map <String, Long> projects = getProjects();
        Map<String, Long> projectsWithTime = new HashMap<String, Long>();
        for (Map.Entry<String,Long> entry : projects.entrySet()){
            List <Long> tasksForProject = new ArrayList<Long>();
            long sumTimeOfTasks = 0;
            taskList.stream().filter(t -> t.project.equals(entry.getKey())).forEach(t->tasksForProject.add(t.id));
            for(Long id : tasksForProject){
                sumTimeOfTasks += workLogs.stream().filter(w -> w.taskId == id).mapToLong(w -> w.timeLogged).sum();
            }
            projectsWithTime.put(entry.getKey(), sumTimeOfTasks);
        }
        return projectsWithTime;
    }

    public Map<String, Long> getProjects(){
        Map <String, Long> projects = new HashMap<String, Long>();
        for(Task task : taskList){
            if(!projects.containsKey(task.project)){
                projects.put(task.project, (long) 0);
            }
        }
        return projects;
    }

    public Map <String, Long> getNumberOfTasksInProjects(){
        Map <String, Long> projects = getProjects();
        Map<String, Long> projectsWithNumberOfTasks = new HashMap<String, Long>();
        for (Map.Entry<String,Long> entry : projects.entrySet()) {
            taskList.stream().filter(t -> t.project.equals(entry.getKey())).forEach(t -> {
                if(projectsWithNumberOfTasks.containsKey(entry.getKey())){
                    projectsWithNumberOfTasks.put(entry.getKey(), projectsWithNumberOfTasks.get(entry.getKey())+1);
                }else{
                    projectsWithNumberOfTasks.put(entry.getKey(), (long)1);
                }
            });
        }
        return projectsWithNumberOfTasks;
    }
}