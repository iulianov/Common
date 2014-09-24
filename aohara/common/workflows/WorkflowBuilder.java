package aohara.common.workflows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

import aohara.common.workflows.tasks.DeleteTask;
import aohara.common.workflows.tasks.FileTransferTask;
import aohara.common.workflows.tasks.path.DefaultPathGen;
import aohara.common.workflows.tasks.path.PathGen;

/**
 * Class with methods for adding standard tasks to a Workflow
 */
public final class WorkflowBuilder {
	
	public static void copy(Workflow workflow, Path src, Path dest) throws MalformedURLException{
		download(workflow, src.toUri().toURL(), dest);
	}
	
	public static void copy(Workflow workflow, Path src, PathGen pathGen) throws MalformedURLException{
		download(workflow, src.toUri().toURL(), pathGen);
	}
	
	public static void download(Workflow workflow, URL url, Path dest){
		workflow.addTask(new FileTransferTask(workflow, url, dest));
	}
	
	public static void download(Workflow workflow, URL url, PathGen pathGen){
		workflow.addTask(new FileTransferTask(workflow, url, pathGen));
	}
	
	public static void delete(Workflow workflow, Path path){
		workflow.addTask(new DeleteTask(workflow, path));
	}
	
	public static void delete(Workflow workflow, PathGen pathGen){
		workflow.addTask(new DeleteTask(workflow, pathGen));
	}
	
	public static void move(Workflow workflow, Path src, Path dest) throws MalformedURLException{
		move(workflow, src, new DefaultPathGen(dest));
	}
	
	public static void move(Workflow workflow, Path src, PathGen destGen) throws MalformedURLException{
		copy(workflow, src, destGen);
		delete(workflow, destGen);
	}
	
	public static void tempDownload(Workflow workflow, URL url, Path dest) throws IOException{
		tempDownload(workflow, url, new DefaultPathGen(dest));
	}
	
	public static void tempDownload(Workflow workflow, URL url, PathGen destGen) throws IOException{
		Path temp = Files.createTempFile(FilenameUtils.getBaseName(url.getPath()), ".tempDownload");
		temp.toFile().deleteOnExit();
		download(workflow, url, temp);
		move(workflow, temp, destGen);
	}
}
