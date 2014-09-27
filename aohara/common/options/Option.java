package aohara.common.options;

import java.util.Collection;
import java.util.LinkedList;

import aohara.common.options.Constraint.InvalidInputException;

public class Option {
	
	private final Collection<Constraint> constraints = new LinkedList<>();
	private final OptionSaveStrategy saveStrat;
	private String value;
	public final String name;
	
	public Option(String name, OptionSaveStrategy saveStrat){
		this(name, null, saveStrat);
	}
	
	public Option(String name, String value, OptionSaveStrategy saveStrat){
		this.name = name;
		this.value = value;
		this.saveStrat = saveStrat;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getValueString(){
		return value != null ? value.toString() : null;
	}
	
	public void testValue(String value) throws InvalidInputException{
		for (Constraint constraint : constraints){
			constraint.check(value);
		}
	}
	
	public void setValue(String value) throws InvalidInputException{		
		testValue(value);
		saveStrat.setValue(value);
	}
	
	public void addConstraint(Constraint constraint){
		constraints.add(constraint);
	}
	
	
}
