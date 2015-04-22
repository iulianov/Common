package aohara.common.config;

public abstract class Constraint {
	
	private final String propertyName;
	
	public Constraint(String propertyName){
		this.propertyName = propertyName;
	}
	
	public abstract void check(Object value) throws InvalidInputException;
	
	@SuppressWarnings("serial")
	public class InvalidInputException extends Exception {
		
		public InvalidInputException(Exception ex){
			super(ex);
		}
		
		public InvalidInputException(String message){
			super(String.format("Property %s: %s", propertyName, message));
		}
		
	}

}
