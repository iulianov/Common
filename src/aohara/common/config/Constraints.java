package aohara.common.config;

import java.io.File;

import javax.swing.JFileChooser;

public class Constraints {
	
	public static class NotNull extends Constraint {

		public NotNull(String propertyName) {
			super(propertyName);
		}

		@Override
		public void check(Object value) throws InvalidInputException {
			if (value == null || value.toString().isEmpty()){
				throw new InvalidInputException("must be non-null");
			}
		}
	}
	
	public static class EnsureInt extends Constraint {
		
		private final Integer min, max;

		public EnsureInt(String propertyName, Integer min, Integer max) {
			super(propertyName);
			this.min = min != null ? min : Integer.MIN_VALUE;
			this.max = max != null ? max : Integer.MAX_VALUE;
		}

		@Override
		public void check(Object value) throws InvalidInputException {
			try{
				int val = Integer.parseInt(value.toString());
				if (val < min || val > max){
					throw new InvalidInputException(String.format("%d is not in range (%d, %d)", val, min, max));
				}
			} catch(NumberFormatException ex){
				throw new InvalidInputException(ex);
			}
		}
	}
	
	public static class EnsureIsFile extends Constraint {

		public final int fileSelectionMode;
		
		public EnsureIsFile(String propertyName, int fileSelectionMode) {
			super(propertyName);
			this.fileSelectionMode = fileSelectionMode;
		}

		@Override
		public void check(Object value) throws InvalidInputException {
			if (!(value instanceof File)){
				throw new InvalidInputException(value + " must be a file");
			} else if (fileSelectionMode == JFileChooser.FILES_ONLY && !((File)value).isFile()){
				throw new InvalidInputException(value + " must be a file");
			} else if (fileSelectionMode == JFileChooser.DIRECTORIES_ONLY && !((File)value).isDirectory()){
				throw new InvalidInputException(value + " must be a directory");
			}
		}
	}
}
