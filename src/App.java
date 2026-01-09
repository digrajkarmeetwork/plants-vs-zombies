import controller.Controller;
import model.Board;
import model.CommandManager;
import view.View;

public class App {
	public static void main(String args[]) {
		CommandManager cm = new CommandManager();
		Board m = new Board(cm);
		View v = new View();
		Controller c = new Controller(m,v,cm);
		c.initController();
	}
}
