package aohara.common.config;

import java.util.Collection;
import java.util.LinkedList;

import aohara.common.config.Constraint.InvalidInputException;

public class Option {
	
	private final Collection<Constraint> constraints = new LinkedList<>();
	public final String name;
	private Config config;
	
	public Option(String name){
		this.name = name;
	}
	
	public void setConfig(Config config){
		this.config = config;
	}
	
	public String getValue(){
		return config.getProperty(name);
	}
	
	public void testValue(String value) throws InvalidInputException{
		for (Constraint constraint : constraints){
			constraint.check(value);
		}
	}
	
	public void setValue(String value) throws InvalidInputException{		
		testValue(value);
		config.setProperty(name, value);
	}
	
	public void addConstraint(Constraint constraint){
		constraints.add(constraint);
	}
}
