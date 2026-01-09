package view;
import junit.framework.TestCase;
import model.GenericZombie;

public class ViewTest extends TestCase {
	
	private View view;
	
	protected void setUp() {
		view= new View();
	}
	
	public void testView() {
		assertNotNull(view.getMenuList());
		assertNotNull(view.getMenu());
		assertNotNull(view.getHelp());
		assertNotNull(view. getStart());
		assertNotNull(view.getRestart());
		assertNotNull(view.getCoins());
		assertNotNull(view.getUndoTurn());
		assertNotNull(view.getEndTurn());
		assertNotNull(view.getRedoTurn());
		assertNotNull(view.getGridLayoutButtons());
		assertNotNull(view.getButtons());
	}
}