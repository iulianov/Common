package aohara.common.config;

import aohara.common.config.Constraint.InvalidInputException;

public class EditableProperty extends Property {
	
	
	
	public EditableProperty(String name, boolean hidden, Class<?> type){
		super(name, hidden, type);
	}
	
	public void testValue(Object value) throws InvalidInputException{
		for (Constraint constraint : constraints){
			constraint.check(value);
		}
	}
	
	public void test() throws InvalidInputException {
		testValue(getValue());
	}
	
	public void setValue(Object value) throws InvalidInputException{	
		testValue(value);
		this.value = value != null ? value.toString() : null;
	}
	
	public void addConstraint(Constraint constraint){
		constraints.add(constraint);
	}
}
