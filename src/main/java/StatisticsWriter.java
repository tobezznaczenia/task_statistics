import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsWriter {

    private Statistics statistics;

    public StatisticsWriter(Statistics statistics) {
        this.statistics = statistics;
    }

    public void writeLoggedTimeForEpics(){
        Map<Long, Long> epicsWithLoggedTime = statistics.getLoggedTimeForEpics();
        FileWriter writer = null;
        try {
            writer = new FileWriter("data/statistics/logged_time_for_epics.txt");
            writer.write("\t\tEpics"+"\n\n"+"Epic id"+"\t\t"+"Spent time"+"\n\n");
            for (Map.Entry<Long,Long> entry : epicsWithLoggedTime.entrySet()){
                writer.write(entry.getKey()+"\t\t\t"+entry.getValue()+"\n");
            }
            writer.close();
            System.out.println("File with logged time Epics has been created properly.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeLoggedTimeForProjects(){
        Map<String, Long> loggedTimeForProjects = statistics.getLoggedTimeForProjects();
        FileWriter writer = null;
        try {
            writer = new FileWriter("data/statistics/logged_time_for_projects.txt");
            writer.write("\t\tProjects and spent times\n\n");
            writer.write(String.format("%-30s%s", "Project name", "Spent time\n\n"));
            for (Map.Entry<String,Long> entry : loggedTimeForProjects.entrySet()){
                writer.write(String.format("%-30s%s",entry.getKey(), entry.getValue()+"\n"));
            }
            writer.close();
            System.out.println("File with logged time for projects has been created properly.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeNumberOfTasksInProjects(){
        Map<String, Long> numberOfTasksInProjects = statistics.getNumberOfTasksInProjects();
        FileWriter writer = null;
        try {
            writer = new FileWriter("data/statistics/number_of_tasks_in_projects.txt");
            writer.write("\t\tProjects and tasks"+"\n\n"+"Project name"+"\t\t"+"Number of tasks"+"\n\n");
            for (Map.Entry<String,Long> entry : numberOfTasksInProjects.entrySet()){
                writer.write(entry.getKey()+"\t\t\t\t\t"+entry.getValue()+"\n");
            }
            writer.close();
            System.out.println("File with number of tasks in projects has been created properly.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUsersLoggedTime(){
        Map<String, Long> usersLoggedTime = statistics.getUsersLoggedTime();
        FileWriter writer = null;
        try {
            writer = new FileWriter("data/statistics/users_logged_time.txt");
            writer.write("Users\n\n");
            writer.write(String.format("%-30s%s", "Name", "Spent time\n\n"));
            for (Map.Entry<String,Long> entry : usersLoggedTime.entrySet()){
                writer.write(String.format("%-30s%s",entry.getKey(), entry.getValue()+"\n"));
            }
            writer.close();
            System.out.println("File with user's logged time has been created properly.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLoggedTimedForEpicsInProjects(){
        Map<String, Long> projects = statistics.getProjects();
        Map<Long, String> epicsWithProjects = statistics.getEpicsAndProjects();
        Map<Long, Long> epicsWithSpentTimes = statistics.getLoggedTimeForEpics();
        boolean projectHasEpic;
        FileWriter writer = null;
        try {
            writer = new FileWriter("data/statistics/logged_time_for_epics_in_projects.txt");
            for (Map.Entry<String, Long> entry : projects.entrySet()) {
                projectHasEpic = false;
                writer.write("\t\tProject " + entry.getKey()+"\n\n");
                writer.write(String.format("%-30s%s","Task id", "Spent time"+"\n"));
                epicsWithProjects = new TreeMap<>(epicsWithProjects);
                for (Map.Entry<Long, String> entryEpicWithProject : epicsWithProjects.entrySet()) {
                    if(entryEpicWithProject.getValue().equals(entry.getKey())){
                        projectHasEpic = true;
                        for (Map.Entry<Long, Long> entryEpicsWithSpentTime : epicsWithSpentTimes.entrySet()) {
                            if(entryEpicsWithSpentTime.getKey()==entryEpicWithProject.getKey()){
                                writer.write(String.format("%-30s%s",entryEpicsWithSpentTime.getKey(), entryEpicsWithSpentTime.getValue()+"\n"));
                            }
                        }
                    }
                }
                if(!projectHasEpic)
                    writer.write("No tasks");
            }
            writer.close();
            System.out.println("File with epic's logged time in projects has been created properly.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeLoggedTimeForTasksDataOrdered(){
        Map<Long, Map<String, String>> tasksAndDate = statistics.getTasksAndDates();
        FileWriter writer = null;
        try {
            writer = new FileWriter("data/statistics/tasks_logged_time_ordered_by_data.txt");
            writer.write(" Logged time for tasks ordered by data");
            for (Map.Entry<Long, Map<String, String>> entry : tasksAndDate.entrySet()){
                writer.write("\n\n\t\t\t\tTask id: "+entry.getKey()+"\n\n");
                writer.write(String.format("%-30s%s", "Date", "Logged time (hours.minutes)"+"\n\n"));
                Map<String, String> ordered = new TreeMap<>(entry.getValue());
                for (Map.Entry<String,String> dateEntry : ordered.entrySet()){
                    writer.write(String.format("%-30s%s", dateEntry.getKey(), dateEntry.getValue()+"\n"));
                }
            }
            writer.close();
            System.out.println("File with logged time ordered by date has been created properly.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
