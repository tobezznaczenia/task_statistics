import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public Map<String, Long> getUsersLoggedTime(){
        Map<String, Long> userWithLoggedTime = new HashMap<String, Long>();
        for(WorkLog workLog : workLogs){
            if(userWithLoggedTime.containsKey(workLog.author)){
                userWithLoggedTime.put(workLog.author, (userWithLoggedTime.get(workLog.author)+workLog.timeLogged));
            }else{
                userWithLoggedTime.put(workLog.author, workLog.timeLogged);
            }
        }

        return userWithLoggedTime;
    }

    public Map<Long, String> getEpicsAndProjects() {
        Set<Long> epics = getDistinctEpics();
        Map<Long, String> epicsWithProjects = new HashMap<Long, String>();
        for (Long id : epics) {
            String project = taskList.stream().filter(t -> t.id == id).findFirst().get().project;
            epicsWithProjects.put(id, project);
        }
        return epicsWithProjects;
    }

    public Set<Long> getDistinctEpics(){
        Set<Long> distinctTasksId = new HashSet<>();
        for(Task task : taskList){
            if(task.parent==-1)
                distinctTasksId.add(task.id);
        }
        return distinctTasksId;
    }

    public Map<Long, Map<String, String>> getTasksAndDates(){
        Map<Long, List<String>> usersAndTasks = getUsersAndTasks();
        Map<Long, Map<String, String>> tasksWithDates = new HashMap<>();
        SimpleDateFormat sf = new SimpleDateFormat("H.mm");
        sf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        for (Map.Entry<Long, List<String>> entry : usersAndTasks.entrySet()) {
            Map<String, Long> datesWithHours = new HashMap<>();
            Map<String, String> tasksWithHours = new HashMap<>();

            for(String author : entry.getValue()){
                workLogs.stream().filter(w->(w.author==author && w.taskId==entry.getKey())).forEach(w->{
                    getDatesAndHours(w.date, w.timeLogged, datesWithHours);
                });
            }

            for (Map.Entry<String, Long> dateWithHour : datesWithHours.entrySet()){
                tasksWithHours.put(dateWithHour.getKey(), String.valueOf(sf.format(new Date(dateWithHour.getValue()))+"h"));
            }
            tasksWithDates.put(entry.getKey(), tasksWithHours);
        }
        return tasksWithDates;
    }

    public Map<Long, List<String>> getUsersAndTasks(){
        Map<Long, List<String>> usersAndTasks = new HashMap<Long, List<String>>();
        Set<Long> distinctTasksId = getDistinctTasks();
        for(Long task : distinctTasksId){
            List<String> users = new ArrayList<String>();
            workLogs.stream().filter(w -> w.taskId==task).forEach(w->users.add(w.author));
            usersAndTasks.put(task, users);
        }
        return usersAndTasks;
    }

    public Set<Long> getDistinctTasks(){
        Set<Long> distinctTasksId = new HashSet<>();
        for(Task task : taskList){
            distinctTasksId.add(task.id);
        }
        return distinctTasksId;
    }

    public void getDatesAndHours(Long timestamp, Long time, Map<String, Long> datesWithHours){
        Long timeInMilliseconds = time*3600000;

        long timeSpent = (long)0;
        String totalTimeSpent = "";

        Date startHour = null;
        Date endHour = null;

        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Date dateAfterTimeSpend = new Date(timestamp+timeInMilliseconds);
        Calendar calendarAfterTimeSpend = Calendar.getInstance();
        calendarAfterTimeSpend.setTime(dateAfterTimeSpend);

        Calendar calendarNextDay = Calendar.getInstance();
        calendarNextDay.setTime(date);
        calendarNextDay.add(Calendar.DATE, 1);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            startHour = format.parse(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(calendarAfterTimeSpend.get(Calendar.DAY_OF_YEAR) == calendarNextDay.get(Calendar.DAY_OF_YEAR)
                && calendarAfterTimeSpend.get(Calendar.YEAR) == calendarNextDay.get(Calendar.YEAR)){
            try {
                endHour = format.parse("23:59");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ;
            if(datesWithHours.containsKey(dateToString(date))){
                timeSpent = endHour.getTime()-startHour.getTime();
                datesWithHours.put(dateToString(date), datesWithHours.get(dateToString(date))+timeSpent);
            }else{
                timeSpent = endHour.getTime()-startHour.getTime();
                datesWithHours.put(dateToString(date), timeSpent);
            }
            try {
                startHour = format.parse("00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                endHour = format.parse(calendarAfterTimeSpend.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(datesWithHours.containsKey(dateToString(dateAfterTimeSpend))){
                timeSpent = endHour.getTime()-startHour.getTime();
                datesWithHours.put(dateToString(date), datesWithHours.get(dateToString(date))+timeSpent);
            }else{
                timeSpent = endHour.getTime()-startHour.getTime();
                datesWithHours.put(dateToString(date), timeSpent);
            }
        }else{
            try {
                endHour = format.parse(calendarAfterTimeSpend.get(Calendar.HOUR_OF_DAY)+":"+calendarAfterTimeSpend.get(Calendar.MINUTE));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(datesWithHours.containsKey(dateToString(date))){
                timeSpent = endHour.getTime()-startHour.getTime();
                datesWithHours.put(dateToString(date),datesWithHours.get(dateToString(date))+timeSpent);
            }else{
                timeSpent = endHour.getTime()-startHour.getTime();
                datesWithHours.put(dateToString(date), timeSpent);
            }
        }
    }

    public String dateToString(Date date){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(date);
    }


}