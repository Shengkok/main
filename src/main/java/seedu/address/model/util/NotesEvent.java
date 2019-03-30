package seedu.address.model.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.LoginEvent;
import seedu.address.model.notes.Content;
import seedu.address.model.notes.Heading;
import seedu.address.model.notes.Notes;
import seedu.address.storage.JsonNotesStorage;

public class NotesEvent {

    private JsonNotesStorage notesStorage;
    private Notes notes;
    private Logger logger = LogsCenter.getLogger(LoginEvent.class);

    public NotesEvent() {
        // Recognises the path as notes.json
        final Path notesPath = Paths.get("notes.json");
        Heading heading = notes.getHeading();
        Content content = notes.getContent();
        notes = new Notes(heading, content);

        try {
            // initialize the storage
            notesStorage = new JsonNotesStorage(notesPath);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        newNotes(notes);
    }

    public void newNotes(Notes notes) {
        String newheading = notes.getHeading().toString();
        String newcontent = notes.getContent().toString();

        try {
            notesStorage.newNotes(newheading,newcontent);
        } catch (IOException e) {
            logger.warning("User storage is unable to read or write to Json file" + StringUtil.getDetails(e));
        }

    }


}
