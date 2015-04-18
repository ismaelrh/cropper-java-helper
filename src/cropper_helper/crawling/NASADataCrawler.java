package cropper_helper.crawling;


import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vividsolutions.jts.geom.Coordinate;
import cropper_helper.crawling.threads.ThermalAnomaliesTask;
import cropper_helper.data.ThermalSingleValue;
import cropper_helper.database.DatabaseHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.lightcouch.NoDocumentException;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by diego on 14/4/15.
 */
public class NASADataCrawler {

    public static Map<String,Object> updateThermalAnomaly(Coordinate mid_point, long _id) {
        ArrayList<ThermalSingleValue> values = new ArrayList<ThermalSingleValue>();
        try {
            try {
                JsonObject old_data = DatabaseHelper.getThermalDocument(_id);
                Map<String,Object> result = new ObjectMapper().readValue(old_data.toString(), HashMap.class);
                int day = (int)result.get("day");

                if(day!=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    DatabaseHelper.removeDoc(old_data);
                } else {
                    return result;
                }
            } catch (NoDocumentException e1) {e1.printStackTrace();}

            String urlString = "https://api.data.gov/nasa/planetary/earth/temperature/coords?lon=" +
                    mid_point.x%180 + "&lat=" +
                    mid_point.y%180 + "&begin=1990&end="+ Calendar.getInstance().get(Calendar.YEAR)+"&api_key=DEMO_KEY";
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();


            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(is, Map.class);
            jsonMap.put("_id",String.valueOf(_id));
            jsonMap.put("day",Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            DatabaseHelper.storeDoc(jsonMap);
            System.out.println("Stored in database");
            return jsonMap;

        } catch (Exception e) {
            System.out.print("Unable to update information about [" + mid_point.x + "," + mid_point.y + "].\n");
            e.printStackTrace();
            return null;
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