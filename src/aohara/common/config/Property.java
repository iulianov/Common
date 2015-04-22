package aohara.common.config;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

public abstract class Property {
	
	protected final Collection<Constraint> constraints = new LinkedList<>();
	final Class<?> type;
	public final String name;
	public final boolean hidden;
	protected String value;
	
	public Property(String name, boolean hidden, Class<?> type){
		this.name = name;
		this.hidden = hidden;
		this.type = type;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getValue(){
		if (type.isAssignableFrom(File.class)){
			return getValueAsFile();			
		} else if (type.isAssignableFrom(Boolean.class)){
			return getValueAsBool();
		} else if (type.isAssignableFrom(Integer.class)){
			return getValueAsInt();
		}
		return getValueAsString();
	}
	
	private boolean isNull(){
		return getValueAsString() == null;
	}
	
	public String getValueAsString(){
		return value;
	}
	
	public File getValueAsFile(){
		return isNull() ? null : new File(getValueAsString());
	}
	
	public Boolean getValueAsBool(){
		return isNull() ? null : Boolean.parseBoolean(getValueAsString());
	}
	
	public Integer getValueAsInt(){
		return isNull() ? null : Integer.parseInt(getValueAsString());
	}
	
	public Collection<Constraint> getConstraints(){
		return new LinkedList<>(constraints);
	}
	
	public Constraint findConstraint(Class<? extends Constraint> constraintType) throws IllegalArgumentException {
		for (Constraint c : getConstraints()){
			if (constraintType.isAssignableFrom(c.getClass())){
				return c;
			}
		}
		throw new IllegalArgumentException();
	}
}
