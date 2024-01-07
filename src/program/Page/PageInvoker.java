package program.page;

import program.admin.Manager;

import java.util.Stack;

public class PageInvoker implements PageCommand {
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private Page page;

    private boolean actionDone = false;

    /**
     * @param pageRef reference to the page
     * @param command the command to be saved
     */
    public void addCommandHistory(final Page pageRef, final String command) {
        this.page = pageRef;
        if (actionDone) {
            actionDone = false;
            return;
        }
        undoStack.push(command);
        this.page.setPreviousPage(command);
        redoStack.clear();
    }

    /**
     * undo function
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            actionDone = true;
            String redoneCommand = undoStack.pop();
            String undoneCommand = undoStack.peek();
            page.changePage(undoneCommand);
            redoStack.push(redoneCommand);
            Manager.getPartialResult().put("message", "The user " + page.getOwner()
                    + " has navigated successfully to the previous page.");
        } else {
            Manager.getPartialResult().put("message", "There are no pages left to go back.");
        }
    }

    /**
     * redo function
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            actionDone = true;
            String redoneCommand = redoStack.pop();
            page.changePage(redoneCommand);
            undoStack.push(redoneCommand);
            Manager.getPartialResult().put("message", "The user " + page.getOwner()
                    + " has navigated successfully to the next page.");
        } else {
            Manager.getPartialResult().put("message", "There are no pages left to go forward.");
        }
    }
}
