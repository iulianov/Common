package aohara.common.options;

import aohara.common.AbstractConfig;

public interface OptionSaveStrategy {
	
	public void setValue(String value);
	
	public static class ConfigStrategy implements OptionSaveStrategy {

		private final AbstractConfig config;
		private final String attributeName;
		
		public ConfigStrategy(AbstractConfig config, String attributeName){
			this.config = config;
			this.attributeName = attributeName;
		}

		@Override
		public void setValue(String value) {
			config.setProperty(attributeName, value);
		}
	}
	
	public static class NullStrategy implements OptionSaveStrategy {

		@Override
		public void setValue(String value) {
			// No Action
		}
		
	}

}
