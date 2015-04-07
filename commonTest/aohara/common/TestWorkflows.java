package aohara.common;

import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import aohara.common.workflows.ProgressPanel;
import aohara.common.workflows.WorkflowBuilder;

public class TestWorkflows {
	
	public static void main(String[] args) throws Exception{
		
		Executor executor = Executors.newFixedThreadPool(3);
		
		ProgressPanel panel = new ProgressPanel();
		panel.toDialog("Foo");
		
		WorkflowBuilder builder = new WorkflowBuilder("test");
		builder.tempDownload(
			new URL("http://download.tuxfamily.org/notepadplus/6.6.8/npp.6.6.8.Installer.exe"),
			Paths.get(System.getProperty("user.home"), "Documents")
		);
		builder.addListener(panel);
		builder.execute(executor);
	}

}
