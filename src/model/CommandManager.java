package model;

import java.io.Serializable;
import java.util.Stack;

public class CommandManager implements Serializable{
	 
    private Stack<Command> undoStack = new Stack<Command>();
    private Stack<Command> redoStack = new Stack<Command>();
 
    /**
     * This method executes a command.
     * @param command
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }
 
    /**
     * This method checks if a undo is available.
     * @return boolean
     */
    public boolean isUndoAvailable() {
        return !undoStack.empty();
    }
 
    /**
     * This method undoes a command.
     */
    public void undo() {
    	//Cannot execute an undo if the stack is empty
        if (undoStack.empty())
        	return;
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }
 
    /**
     * This method checks if a redo is available
     * @return boolean
     */
    public boolean isRedoAvailable() {
        return !redoStack.empty();
    }
 
    /**
     * This method redoes a command
     * @return boolean
     */
    public void redo() {
    	//Cannot execute a redo if the stack is empty
        if (redoStack.empty())
        	return;
        Command command = redoStack.pop();
        command.redo();
        undoStack.push(command);
    }
}
