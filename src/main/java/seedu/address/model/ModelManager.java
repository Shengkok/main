package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.login.User;
import seedu.address.model.login.Username;
import seedu.address.model.notes.Notes;
import seedu.address.model.notes.exceptions.NotesNotFoundException;
import seedu.address.model.task.Task;
import seedu.address.model.task.exceptions.TaskNotFoundException;
import seedu.address.model.util.NotesEvent;

/**
 * Represents the in-memory model of the task manager data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedTaskManager versionedTaskManager;
    private final UserPrefs userPrefs;
    private final FilteredList<Task> filteredTasks;
    private final FilteredList<Notes> filteredNotes;
    private final SimpleObjectProperty<Task> selectedTask = new SimpleObjectProperty<>();
    private final LoginEvent loginEvent;
    private final NotesEvent notesEvent;
    private final SimpleObjectProperty<Notes> selectedNotes = new SimpleObjectProperty<>();

    /**
     * Initializes a ModelManager with the given taskManager and userPrefs.
     */
    public ModelManager(ReadOnlyTaskManager taskManager, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(taskManager, userPrefs);

        logger.fine("Initializing with task manager: " + taskManager + " and user prefs " + userPrefs);

        versionedTaskManager = new VersionedTaskManager(taskManager);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredTasks = new FilteredList<>(versionedTaskManager.getTaskList());
        filteredTasks.addListener(this::ensureSelectedTaskIsValid);
        filteredNotes = new FilteredList<>(versionedTaskManager.getNotesList());
        loginEvent = new LoginEvent();
        notesEvent = new NotesEvent();
    }

    public ModelManager() {
        this(new TaskManager(), new UserPrefs());
    }

    //=========== Login Information ==================================================================================

    @Override
    public boolean getLoginStatus() {
        return loginEvent.getLoginStatus();
    }

    @Override
    public boolean getAdminStatus() {
        return loginEvent.getAdminStatus();
    }

    @Override
    public boolean userExists(User user) {
        requireNonNull(user);
        return loginEvent.userExists(user);
    }

    @Override
    public boolean accountExists() {
        return loginEvent.accountExists();
    }

    @Override
    public void deleteAccount() {
        loginEvent.deleteAccount();
    }


    @Override
    public void newUser(User user) {
        requireNonNull(user);
        loginEvent.newUser(user);
    }

    @Override
    public void loginUser(User loginInfo) {
        requireNonNull(loginInfo);
        loginEvent.loginUser(loginInfo);
    }

    @Override
    public Username getUsername() {
        return loginEvent.getUsername();
    }

    @Override
    public void logout() {
        loginEvent.logout();
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getTaskManagerFilePath() {
        return userPrefs.getTaskManagerFilePath();
    }

    @Override
    public void setTaskManagerFilePath(Path taskManagerFilePath) {
        requireNonNull(taskManagerFilePath);
        userPrefs.setTaskManagerFilePath(taskManagerFilePath);
    }

    //=========== TaskManager ================================================================================

    @Override
    public ReadOnlyTaskManager getTaskManager() {
        return versionedTaskManager;
    }

    @Override
    public void setTaskManager(ReadOnlyTaskManager taskManager) {
        versionedTaskManager.resetData(taskManager);
    }

    @Override
    public boolean hasTask(Task task) {
        requireNonNull(task);
        return versionedTaskManager.hasTask(task);
    }

    @Override
    public void deleteTask(Task target) {
        versionedTaskManager.removeTask(target);
    }

    @Override
    public void addTask(Task task) {
        versionedTaskManager.addTask(task);
        updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
    }

    @Override
    public void setTask(Task target, Task editedTask) {
        requireAllNonNull(target, editedTask);

        versionedTaskManager.setTask(target, editedTask);
    }

    @Override
    public void sortTask(String attribute) {
        versionedTaskManager.sortTask(attribute);
        updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
    }

    //================= Notes==============================================================================

    @Override
    public boolean hasNotes(Notes notes) {
        requireNonNull(notes);
        return versionedTaskManager.hasNotes(notes);
    }

    @Override
    public void addNotes(Notes notes) {
        versionedTaskManager.addNotes(notes);
        updateFilteredNotesList(PREDICATE_SHOW_ALL_NOTES);

    }


    //=========== Filtered Task List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Task} backed by the internal list of
     * {@code versionedTaskManager}
     */
    @Override
    public ObservableList<Task> getFilteredTaskList() {
        return filteredTasks;
    }

    @Override
    public void updateFilteredTaskList(Predicate<Task> predicate) {
        requireNonNull(predicate);
        filteredTasks.setPredicate(predicate);
    }

    //=================Filtered Notes List Accessors=========================================

    /**
     * Returns an unmodifiable view of the list of {@code Task} backed by the internal list of
     * {@code versionedTaskManager}
     */
    @Override
    public ObservableList<Notes> getFilteredNotesList() {
        return filteredNotes;
    }

    @Override
    public void updateFilteredNotesList(Predicate<Notes> predicate) {
        requireNonNull(predicate);
        filteredNotes.setPredicate(predicate);
    }


    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoTaskManager() {
        return versionedTaskManager.canUndo();
    }

    @Override
    public boolean canRedoTaskManager() {
        return versionedTaskManager.canRedo();
    }

    @Override
    public void undoTaskManager() {
        versionedTaskManager.undo();
    }

    @Override
    public void redoTaskManager() {
        versionedTaskManager.redo();
    }

    @Override
    public void commitTaskManager() {
        versionedTaskManager.commit();
    }

    //=========== Selected task ===========================================================================

    @Override
    public ReadOnlyProperty<Task> selectedTaskProperty() {
        return selectedTask;
    }

    @Override
    public Task getSelectedTask() {
        return selectedTask.getValue();
    }

    @Override
    public void setSelectedTask(Task task) {
        if (task != null && !filteredTasks.contains(task)) {
            throw new TaskNotFoundException();
        }
        selectedTask.setValue(task);
    }

    /**
     * Ensures {@code selectedTask} is a valid task in {@code filteredTasks}.
     */
    private void ensureSelectedTaskIsValid(ListChangeListener.Change<? extends Task> change) {
        while (change.next()) {
            if (selectedTask.getValue() == null) {
                // null is always a valid selected task, so we do not need to check that it is valid anymore.
                return;
            }

            boolean wasSelectedTaskReplaced = change.wasReplaced() && change.getAddedSize() == change.getRemovedSize()
                    && change.getRemoved().contains(selectedTask.getValue());
            if (wasSelectedTaskReplaced) {
                // Update selectedTask to its new value.
                int index = change.getRemoved().indexOf(selectedTask.getValue());
                selectedTask.setValue(change.getAddedSubList().get(index));
                continue;
            }

            boolean wasSelectedTaskRemoved = change.getRemoved().stream()
                    .anyMatch(removedTask -> selectedTask.getValue().isSameTask(removedTask));
            if (wasSelectedTaskRemoved) {
                // Select the task that came before it in the list,
                // or clear the selection if there is no such task.
                selectedTask.setValue(change.getFrom() > 0 ? change.getList().get(change.getFrom() - 1) : null);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedTaskManager.equals(other.versionedTaskManager)
                && userPrefs.equals(other.userPrefs)
                && filteredTasks.equals(other.filteredTasks)
                && Objects.equals(selectedTask.get(), other.selectedTask.get());
    }

    //============ Selected Notes=============================================

    @Override
    public ReadOnlyProperty<Notes> selectedNotesProperty() {
        return selectedNotes;
    }

    @Override
    public Notes getSelectedNotes() {
        return selectedNotes.getValue();
    }

    @Override
    public void setSelectedNotes(Notes notes) {
        if (notes != null && !filteredNotes.contains(notes)) {
            throw new NotesNotFoundException();
        }
        selectedNotes.setValue(notes);
    }

}
