public class TestStatistics {

    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        Statistics statistics = new Statistics(dataLoader.loadWorkLogsData(), dataLoader.loadTasksData());

        StatisticsWriter statisticsWriter = new StatisticsWriter(statistics);
        statisticsWriter.writeLoggedTimeForEpics();
        statisticsWriter.writeLoggedTimeForProjects();
        statisticsWriter.writeNumberOfTasksInProjects();
        statisticsWriter.writeUsersLoggedTime();
        statisticsWriter.writeLoggedTimedForEpicsInProjects();
        statisticsWriter.writeLoggedTimeForTasksDataOrdered();
    }
}
