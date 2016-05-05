package ru.ifmo.acm.mainscreen;

import ru.ifmo.acm.ContextListener;
import ru.ifmo.acm.datapassing.*;

import static ru.ifmo.acm.mainscreen.BreakingNews.MainScreenBreakingNews.getUpdaterThread;

/**
 * Created by Aksenov239 on 15.11.2015.
 */
public class MainScreenData {
    public static MainScreenData getMainScreenData() {
        if (mainScreenData == null) {
            mainScreenData = new MainScreenData();
            //new DataLoader().frontendInitialize();
            //Start update
            Utils.StoppedThread updater = new Utils.StoppedThread(new Utils.StoppedRunnable() {
                public void run() {
                    while (true) {
                        mainScreenData.update();
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            updater.start();
            ContextListener.addThread(updater);

            Utils.StoppedThread breakingNewsTableUpdater = getUpdaterThread();
            breakingNewsTableUpdater.start();
            ContextListener.addThread(breakingNewsTableUpdater);
        }
        return mainScreenData;
    }

    private MainScreenData() {
        advertisementData = new AdvertisementData();
        personData = new PersonData();
        standingsData = new StandingsData();
        teamData = new TeamData();
//        cameraData = new CameraData();
        clockData = new ClockData();
        splitScreenData = new SplitScreenData();
        breakingNewsData = new BreakingNewsData();
        queueData = new QueueData();
    }

    public void update() {
        advertisementData.update();
        personData.update();
        standingsData.update();
        breakingNewsData.update();
        teamData.update();
    }

    private static MainScreenData mainScreenData;

    public static MainScreenProperties getProperties() {
        return mainScreenData.mainScreenProperties;
    }

    public AdvertisementData advertisementData;
    public ClockData clockData;
    public PersonData personData;
    public StandingsData standingsData;
    public TeamData teamData;
    public CameraData cameraData;
    public SplitScreenData splitScreenData;
    public BreakingNewsData breakingNewsData;
    public QueueData queueData;

    private final MainScreenProperties mainScreenProperties = new MainScreenProperties();
}
