package cropper_helper.crawling;

import com.google.gson.stream.JsonReader;
import com.vividsolutions.jts.geom.Coordinate;
import cropper_helper.CropperHelper;
import cropper_helper.data.ThermalSingleValue;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Formatter;

/**
 * Created by diego on 14/4/15.
 */
public class NASADataCrawler {

    public static void main(String args[]) {

        updateThermalAnomaly(new Coordinate(12, 12), "012345678");
    }

    public static void updateThermalAnomaly(Coordinate mid_point, String _id) {
        ArrayList<ThermalSingleValue> values = new ArrayList<ThermalSingleValue>();
        try {
            String urlString = "https://api.data.gov/nasa/planetary/earth/temperature/coords?lon=" +
                    mid_point.x + "&lat=" +
                    mid_point.y + "&begin=1990&end=2005&api_key=DEMO_KEY";
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            JsonReader reader = new JsonReader(new InputStreamReader(is));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results")) {
                    values = readArray(reader);
                } else {
                    reader.nextInt();
                }
            }
            reader.endObject();


        } catch (IOException e) {
            System.out.print("Impossible to update [" + mid_point.x + "," + mid_point.y + "].\n");
        }

        URL location = CropperHelper.class.getProtectionDomain().getCodeSource().getLocation();
        String[] path = location.getPath().split("/");
        String final_path = "";
        int i = 0;
        do {
            i++;
            final_path += "/"+path[i];
        } while(!path[i].equals("cropper-helper"));
        final_path+="/plots";
        System.out.println(final_path);

        try {
            File f = new File(final_path+"/"+_id+".dat");
            f.createNewFile();
            Formatter in = new Formatter(f);

            for (ThermalSingleValue t : values) {
                in.format("" + t.getYear() + " " + t.getValue()+"\n");
            }
            in.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            System.out.print("gnuplot -e \"set terminal png;plot '"+final_path+"/"+ _id + ".data';\" > "+final_path+"/"+ _id + ".png");
            String[] command = {"/bin/bash", "-c", "gnuplot -e \"set terminal png;set title 'Thermal anomalies';plot '"+final_path+"/"+ _id + ".dat' with line;\" > "+final_path+"/"+ _id + ".png"};
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<ThermalSingleValue> readArray(JsonReader reader) throws IOException {
        ArrayList<ThermalSingleValue> result = new ArrayList<ThermalSingleValue>();
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(readValue(reader));
        }
        reader.endArray();
        return result;
    }

    private static ThermalSingleValue readValue(JsonReader reader) throws IOException {
        Double anomaly = 0.0;
        int year = 0;
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("anomaly")) {
                anomaly = reader.nextDouble();
            } else {
                year = reader.nextInt();
            }
        }
        reader.endObject();
        return new ThermalSingleValue(anomaly, year);
    }
}
