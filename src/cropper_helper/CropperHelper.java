package cropper_helper;

import cropper_helper.crawling.threads.ThermalAnomaliesTask;

import java.util.Timer;
import java.util.TimerTask;

public class CropperHelper {

    private static final long thermal_anomalies_freq_milis = 60*60*24*1000;

    public static void main(String[] args) {
        new Thread(new ListenServer()).start();
        TimerTask thermal_anomalies_task = new ThermalAnomaliesTask();
        Timer thermal_anomalies_timer = new Timer(true);
        thermal_anomalies_timer.scheduleAtFixedRate(thermal_anomalies_task, 0, thermal_anomalies_freq_milis);

        while (true) {
            try {
                Thread.sleep(99999);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}