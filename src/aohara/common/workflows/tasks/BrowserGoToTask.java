package aohara.common.workflows.tasks;

import java.io.IOException;
import java.net.URL;

import aohara.common.Util;

public class BrowserGoToTask extends WorkflowTask {
	
	private final URL url;

	BrowserGoToTask(URL url) {
		super("Directing Browser to " + url);
		this.url = url;
	}
	
	public static void callNow(URL url){
		new BrowserGoToTask(url).call(null);
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return url.openConnection().getContentLength();
	}

	@Override
	public boolean execute() throws Exception {
		Util.goToHyperlink(url);
		return true;
	}

}
