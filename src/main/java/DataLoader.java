import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public List<WorkLog> loadWorkLogsData(){
        List<WorkLog> workLogs = new ArrayList<WorkLog>();
        Gson gson = new Gson();
        Type workLogListType = new TypeToken<ArrayList<WorkLog>>(){}.getType();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("data/worklogs.txt"));
            workLogs = gson.fromJson(bufferedReader, workLogListType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return workLogs;
    }

    public List<Task> loadTasksData(){
        List<Task> tasks = new ArrayList<Task>();
        Gson gson = new Gson();
        Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("data/tasks_java.txt"));
            tasks = gson.fromJson(bufferedReader, taskListType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return tasks;
    }


}
