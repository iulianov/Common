package aohara.common.workflows.tasks;

import java.io.IOException;
import java.net.URL;

import aohara.common.Util;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;

public class BrowserGoToTask extends WorkflowTask {
	
	private final URL url;

	public BrowserGoToTask(URL url) {
		this.url = url;
	}

	@Override
	public boolean call(Workflow workflow) throws IOException {
		Util.goToHyperlink(url);
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return url.openConnection().getContentLength();
	}

	@Override
	public String getTitle() {
		return "Directing Browser to " + url;
	}

}
