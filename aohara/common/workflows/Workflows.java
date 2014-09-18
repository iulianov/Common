package aohara.common.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Factory for creating some pre-defined, standard workflows with tasks.
 */
public final class Workflows {
	
	public static Workflow copy(Path src, Path dest) throws MalformedURLException{
		Workflow workflow = new Workflow(String.format("Copying %s to %s", src.getFileName(), dest));
		workflow.queueCopy(src, dest);
		return workflow;
	}
	
	public static Workflow download(URL url, Path dest){
		Workflow workflow = new Workflow(String.format("Downloading %s", dest.getFileName()));
		workflow.queueDownload(url, dest);
		return workflow;
	}
	
	public static Workflow download(String url, Path dest) throws MalformedURLException{
		return download(new URL(url), dest);
	}
	
	public static Workflow delete(Path path){
		Workflow workflow = new Workflow(String.format("Deleting %s", path));
		workflow.queueDelete(path);
		return workflow;
	}
	
	public static Workflow move(Path src, Path dest) throws MalformedURLException{
		Workflow workflow = new Workflow(String.format("Moving %s to %", src.getFileName(), dest));
		workflow.queueMove(src, dest);
		return workflow;
	}
	
	public static Workflow tempDownload(URL url, Path dest) throws IOException{
		Workflow workflow = new Workflow(String.format("Downloading %s", dest.getFileName()));
		workflow.queueTempDownload(url, dest);
		return workflow;
	}
	
	public static Workflow tempDownload(String url, Path dest) throws IOException {
		return tempDownload(new URL(url), dest);
	}
}
