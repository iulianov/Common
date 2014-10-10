package aohara.common.test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import aohara.common.workflows.ProgressPanel;
import aohara.common.workflows.WorkflowBuilder;
import aohara.common.workflows.tasks.gen.GenFactory;

public class TestWorkflows {
	
	public static void main(String[] args) throws Exception{
		
		Executor executor = Executors.newFixedThreadPool(3);
		
		ProgressPanel panel = new ProgressPanel();
		panel.toDialog("Foo");
		
		WorkflowBuilder builder = new WorkflowBuilder("test");
		builder.tempDownload(
			GenFactory.fromUrl(new URL("http://download.tuxfamily.org/notepadplus/6.6.8/npp.6.6.8.Installer.exe")),
			GenFactory.fromPath(Paths.get(System.getProperty("user.home"), "Documents"))
		);
		builder.addListener(panel);
		builder.execute(executor);
	}

}
