package aohara.common.test;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import aohara.common.options.Constraints;
import aohara.common.options.Option;
import aohara.common.options.OptionInput;
import aohara.common.options.OptionSaveStrategy;
import aohara.common.options.OptionsWindow;

public class TestOptionsWindow {

	public static void main(String[] args){
		Set<OptionInput> optionInputs = new HashSet<>();
		
		Option strOption = new Option("String", new OptionSaveStrategy.NullStrategy());
		strOption.addConstraint(new Constraints.MinLength(strOption, 5));
		optionInputs.add(new OptionInput.TextFieldInput(strOption));
		
		Option intOption = new Option("int option", "1", new OptionSaveStrategy.NullStrategy());
		optionInputs.add(new OptionInput.TextFieldInput(intOption));
		
		Vector<String> choices = new Vector<>();
		choices.add("foo");
		choices.add("bar");
		choices.add("baz");
		Option multiOption = new Option("multi", choices.get(0), new OptionSaveStrategy.NullStrategy());
		optionInputs.add(new OptionInput.ComboBoxInput(multiOption, choices));
		
		new OptionsWindow("options", optionInputs).toDialog();
	}
}
