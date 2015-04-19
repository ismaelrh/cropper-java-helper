package cropper_helper;

import cropper_helper.crawling.threads.ThermalAnomaliesTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CropperHelper {
    private static final long THANOM_STATS_FREQ = 24*60*60*1000;    // 1 day

    public static void main(String[] args) {
        new Thread(new ListenServer()).start();
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(new ThermalAnomaliesTask(), 0, THANOM_STATS_FREQ, TimeUnit.MILLISECONDS);
    }
}