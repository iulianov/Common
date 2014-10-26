package aohara.common.workflows.tasks;

import java.io.IOException;

import aohara.common.Util;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.Workflow.WorkflowTask;
import aohara.common.workflows.tasks.gen.URLGen;

public class BrowserGoToTask extends WorkflowTask {
	
	private final URLGen url;

	public BrowserGoToTask(URLGen url) {
		this.url = url;
	}

	@Override
	public boolean call(Workflow workflow) throws IOException {
		Util.goToHyperlink(url.getURL());
		return true;
	}

	@Override
	public int getTargetProgress() throws IOException {
		return url.getURL().openConnection().getContentLength();
	}

	@Override
	public String getTitle() {
		return "Directing Browser to " + url.getURL();
	}

}
