package aohara.common.options;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Constraints {
	
	public static class MinLength extends NotNull {
		
		private final int length;
		
		public MinLength(Option option, int length){
			super(option);
			this.length = length;
		}

		@Override
		public void check(String value) throws InvalidInputException {			
			if (value.length() < length){
				throw new InvalidInputException(
					String.format(
						"%s must be of minimum length %d.  Was %d",
						name, length, value.length()
					)
				);
			}
		}
	}
	
	public static class NotNull extends Constraint {

		public NotNull(Option option) {
			super(option);
		}

		@Override
		public void check(String value) throws InvalidInputException {
			if (value == null || value.toString().isEmpty()){
				throw new InvalidInputException(name + " must be non-null");
			}
		}
	}
	
	public static class EnsureFloat extends Constraint {

		public EnsureFloat(Option option) {
			super(option);
		}

		@Override
		public void check(String value) throws InvalidInputException {
			try{
				Float.parseFloat(value);
			} catch(NumberFormatException ex){
				throw new InvalidInputException(ex);
			}
		}
	}
	
	public static class EnsureInt extends Constraint {

		public EnsureInt(Option option) {
			super(option);
		}

		@Override
		public void check(String value) throws InvalidInputException {
			try{
				Integer.parseInt(value);
			} catch(NumberFormatException ex){
				throw new InvalidInputException(ex);
			}
		}
	}
	
	public static class EnsurePathExists extends Constraint {
		
		private final boolean mustExist;

		public EnsurePathExists(Option option, boolean mustExist) {
			super(option);
			this.mustExist = mustExist;
		}

		@Override
		public void check(String value) throws InvalidInputException {
			File file = new File(value);
			if (!file.isFile() && !file.isDirectory()){
				throw new InvalidInputException(file + " must be a valid path.");
			} else if (mustExist && !file.exists()){
				throw new InvalidInputException(file + " must exist");
			}
		}
		
	}
	
	public static class EnsureURL extends Constraint {

		public EnsureURL(Option option) {
			super(option);
		}

		@Override
		public void check(String value) throws InvalidInputException {
			try {
				new URL(value);
			} catch (MalformedURLException e) {
				throw new InvalidInputException(e);
			}
		}
	}

}
