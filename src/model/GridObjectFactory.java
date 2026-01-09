package model;

public class GridObjectFactory {

	/**
	 * This method returns a new grid object whose type corresponds to 
	 * the paramater passed
	 * @param s
	 * @return GridObject
	 */
	public static GridObject createNewGridObject(String s) {
		switch(s) {
		case ("SunFlower"): 	
			return new SunFlower();
		case ("VenusFlyTrap"):	
			return new VenusFlyTrap();
		case ("Walnut"):
			return new Walnut();
		case("Potatoe"):
			return new Potatoe();
		case("PeaShooter"):
			return new PeaShooter();
		case ("GenericZombie"):	
			return new GenericZombie();
		case ("FrankTheTank"):
			return new FrankTheTank();
		case("BurrowingBailey"):
			return new BurrowingBailey();
		default:				
			return null;
		}
	}
}
