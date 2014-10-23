package aohara.common.config;

public abstract class Constraint {
	
	public final String name;
	
	public Constraint(Option option){
		this.name = option.name;
	}
	
	public abstract void check(String value) throws InvalidInputException;
	
	@SuppressWarnings("serial")
	public static class InvalidInputException extends Exception {
		
		public InvalidInputException(Exception ex){
			super(ex);
		}
		
		public InvalidInputException(String message){
			super(message);
		}
		
	}

}
