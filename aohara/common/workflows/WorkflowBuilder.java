package aohara.common.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

import aohara.common.workflows.tasks.DeleteTask;
import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.gen.GenFactory;
import aohara.common.workflows.tasks.gen.PathGen;
import aohara.common.workflows.tasks.gen.URIGen;
import aohara.common.workflows.tasks.gen.URLGen;

/**
 * Class with methods for adding standard tasks to a Workflow
 */
public final class WorkflowBuilder {
	
	public static void copy(Workflow workflow, URIGen src, PathGen path) throws MalformedURLException{
		download(workflow, src, path);
	}
	
	public static void download(Workflow workflow, URIGen src, PathGen path){
		workflow.addTask(new FileTransferTask(workflow, src, path));
	}

	public static void delete(Workflow workflow, PathGen pathGen){
		workflow.addTask(new DeleteTask(workflow, pathGen));
	}
	
	public static void move(Workflow workflow, PathGen src, PathGen dest) throws MalformedURLException{
		copy(workflow, src, dest);
		delete(workflow, src);
	}
	
	public static void tempDownload(Workflow workflow, URLGen url, PathGen destGen) throws IOException{
		Path temp = downloadToTemp(workflow, url);
		copy(workflow, GenFactory.fromPath(temp), destGen);
	}
	
	public static Path downloadToTemp(Workflow workflow, URLGen url) throws IOException {
		Path temp = Files.createTempFile(FilenameUtils.getBaseName(url.getURL().getPath()), ".tempDownload");
		temp.toFile().deleteOnExit();
		download(workflow, url, GenFactory.fromPath(temp));
		return temp;
	}
}
