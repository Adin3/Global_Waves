package program.page;

import program.admin.Manager;
import program.command.Command;

import java.util.Stack;

public class PageInvoker implements PageCommand{
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private Page page;

    private boolean actionDone = false;

    public void addCommandHistory(Page page, String command) {
        this.page = page;
        if (actionDone) {
            actionDone = false;
            return;
        }
        undoStack.push(page.getPreviousPage());
        page.setPreviousPage(command);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            actionDone = true;
            String undoneCommand = undoStack.pop();
            page.changePage(undoneCommand);
            redoStack.push(undoneCommand);
            Manager.getPartialResult().put("message", "The user " + page.getOwner()
                    + " has navigated successfully to the previous page.");
        } else {
            Manager.getPartialResult().put("message", "There are no pages left to go back.");
        }
    }

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
