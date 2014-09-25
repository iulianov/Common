package aohara.common.workflows.tasks;

import java.io.IOException;

import aohara.common.Util;
import aohara.common.workflows.Workflow;
import aohara.common.workflows.tasks.gen.URLGen;

public class BrowserGoToTask extends WorkflowTask {
	
	private final URLGen url;

	public BrowserGoToTask(Workflow workflow, URLGen url) {
		super(workflow);
		this.url = url;
	}

	@Override
	public Boolean call() throws Exception {
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
