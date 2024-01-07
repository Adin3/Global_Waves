package program.page;

public interface PageCommand {
    /**
     * redo command
     */
    void redo();

    /**
     * undo command
     */
    void undo();
}
