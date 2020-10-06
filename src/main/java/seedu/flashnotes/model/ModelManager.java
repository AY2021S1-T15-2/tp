package seedu.flashnotes.model;

import static java.util.Objects.requireNonNull;
import static seedu.flashnotes.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.flashnotes.commons.core.GuiSettings;
import seedu.flashnotes.commons.core.LogsCenter;
import seedu.flashnotes.model.flashcard.Flashcard;
import seedu.flashnotes.model.tag.Tag;

/**
 * Represents the in-memory model of the flashnotes data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final FlashNotes flashNotes;
    private final UserPrefs userPrefs;
    private final Map<Tag, FilteredList<Flashcard>> tagFlashcardMap;
    private final FilteredList<Flashcard> filteredFlashcards;

    /**
     * Initializes a ModelManager with the given flashNotes and userPrefs.
     */
    public ModelManager(ReadOnlyFlashNotes flashNotes, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(flashNotes, userPrefs);

        logger.fine("Initializing with flashnotes: " + flashNotes + " and user prefs " + userPrefs);

        this.flashNotes = new FlashNotes(flashNotes);
        this.userPrefs = new UserPrefs(userPrefs);
        this.tagFlashcardMap = new HashMap<>(this.flashNotes.getTagFlashcardMap());
        this.filteredFlashcards = new FilteredList<>(this.flashNotes.getAllFlashcardList());
    }

    public ModelManager() {
        this(new FlashNotes(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
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
    public Path getFlashNotesFilePath() {
        return userPrefs.getFlashNotesFilePath();
    }

    @Override
    public void setFlashNotesFilePath(Path flashNotesFilePath) {
        requireNonNull(flashNotesFilePath);
        userPrefs.setFlashNotesFilePath(flashNotesFilePath);
    }

    //=========== FlashNotes ================================================================================

    @Override
    public void setFlashNotes(ReadOnlyFlashNotes flashNotes) {
        this.flashNotes.resetData(flashNotes);
    }

    @Override
    public ReadOnlyFlashNotes getFlashNotes() {
        return flashNotes;
    }

    @Override
    public boolean hasFlashcard(Tag tag, Flashcard flashcard) {
        requireAllNonNull(tag, flashcard);
        return flashNotes.hasFlashcard(tag, flashcard);
    }

    @Override
    public void deleteFlashcard(Tag tag, Flashcard target) {

        flashNotes.removeFlashcard(tag, target);
    }

    @Override
    public void addFlashcard(Tag tag, Flashcard flashcard) {
        flashNotes.addFlashcard(tag, flashcard);
        updateFilteredFlashcardList(PREDICATE_SHOW_ALL_FLASHCARDS);
    }

    @Override
    public void setFlashcard(Tag tag, Flashcard target, Flashcard editedFlashcard) {
        requireAllNonNull(tag, target, editedFlashcard);

        flashNotes.setFlashcard(tag, target, editedFlashcard);
    }

    //=========== Filtered Flashcard List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Flashcard} backed by the internal list of
     * {@code versionedFlashNotes}
     */
    @Override
    public ObservableList<Flashcard> getFilteredFlashcardList() {
        return filteredFlashcards;
    }

    @Override
    public void updateFilteredFlashcardList(Predicate<Flashcard> predicate) {
        requireNonNull(predicate);
        filteredFlashcards.setPredicate(predicate);
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
        return flashNotes.equals(other.flashNotes)
                && userPrefs.equals(other.userPrefs)
                && filteredFlashcards.equals(other.filteredFlashcards);
    }

}
