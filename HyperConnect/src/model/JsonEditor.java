package model;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonEditor {
    private static final String FILE_NAME = "/Documents/hyperconnect/configs.json";

    public static void writeJSON(JSONObject jsonObject) {
        try (FileWriter fileWriter = new FileWriter(FILE_NAME)) {
            fileWriter.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readJSON() {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try (FileReader fileReader = new FileReader(FILE_NAME)) {
            Object obj = jsonParser.parse(fileReader);
            jsonObject = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    public static void updateJson(String key, String value){
        JSONObject object = readJSON();
        object.replace(key, value);
        writeJSON(object);
    }
}
