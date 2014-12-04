package aohara.common.config;

import java.util.Collection;
import java.util.LinkedList;

import aohara.common.config.Constraint.InvalidInputException;

public class Option {
	
	private final Collection<Constraint> constraints = new LinkedList<>();
	public final String name;
	private String value;
	
	public Option(String name){
		this.name = name;
	}
	
	public String getValue(){
		return value;
	}
	
	public void testValue(String value) throws InvalidInputException{
		for (Constraint constraint : constraints){
			constraint.check(value);
		}
	}
	
	public void setValue(String value) throws InvalidInputException{		
		testValue(value);
		this.value = value;
	}
	
	public void addConstraint(Constraint constraint){
		constraints.add(constraint);
	}
}
