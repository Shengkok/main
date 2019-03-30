package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import seedu.address.commons.core.LogsCenter;

public class JsonNotesStorage {

    private String notesFilePath;

    public JsonNotesStorage(Path filePath) throws IOException {
        notesFilePath = "./" + filePath.toString();
        // if there is no such file (taken in from parameter from notesEvent) then it will call createNotesFile
        if (Files.notExists(filePath)) {
            createNotesFile();
        }

    }

    private void createNotesFile() throws IOException {
        //This one is to set the object in the file, i also dk, just copy from google
        JsonObject jsonObject = new JsonObject();
        writeJson(new Gson(), jsonObject);
    }

    //Creates the file and flush it(i think flush is like to close the file, like how u press the red 'x' at the top left)
    private void writeJson(Gson gson, JsonObject jsonObject) throws IOException {
        // This (jsonobject) u need to refer to my other functions in
        // JsonLoginStorage on how to put string into GSON objects
        // It is in newUser
        String json = gson.toJson(jsonObject);
        FileWriter file = new FileWriter(notesFilePath);
        file.write(json);
        file.flush();
    }

    public void newNotes(String heading, String content) throws IOException {
        JsonObject jsonObject = getJsonObject();
        jsonObject.addProperty(heading,content);

        writeJson(new Gson(), jsonObject);


    }

    /**
     * Convert user accounts as JSON objects
     * @return JSON objects
     * @throws IOException if object cannot be retrieved
     */
    private JsonObject getJsonObject() throws IOException {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader(notesFilePath));

        return jsonElement.getAsJsonObject();
    }

}

