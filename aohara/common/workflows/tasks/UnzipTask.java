package aohara.common.workflows.tasks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import thirdParty.ZipNode;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.ConflictResolver.Resolution;
import aohara.common.workflows.Workflow.WorkflowTask;

/**
 * WorkflowTask to extract selected files from a Zip File.
 * 
 * @author Andrew O'Hara
 */
public class UnzipTask extends WorkflowTask {
	
	private final Path destPath;
	private final ZipNode sourceNode;
	private final ConflictResolver cr;
	
	public UnzipTask(Path destPath, ZipNode node, ConflictResolver cr){
		this.destPath = destPath;
		this.sourceNode = node;
		this.cr = cr;
	}
	
	@Override
	public Boolean call() throws Exception {
		if (destPath.toFile().isFile() && destPath.toFile().exists()){
			Resolution res = cr.getResolution(destPath);
			if (res.equals(Resolution.Overwrite)){
				FileUtils.deleteDirectory(destPath.toFile());
				unzip();
			} else if (res.equals(Resolution.Skip)){
				// Skip Module
			} else {
				throw new IllegalStateException("Uncaught Resolution");
			}
		} else {
			unzip();
		}
		
		return true;
	}
	
	private void unzip() throws IOException {
		try(ZipFile file = sourceNode.reopenZipFile()){
			ZipNode rootNode = sourceNode.getParent() != null ? sourceNode.getParent() : sourceNode;
			unzip(file, sourceNode, rootNode);
			for (ZipNode child : sourceNode.getAllChildren()){
				unzip(file, child, rootNode);
			}
		}
	}
	
	private void unzip(ZipFile file, ZipNode node, ZipNode rootNode) throws IOException{
		if (!node.isDirectory()){
			FileUtils.copyInputStreamToFile(
				file.getInputStream(node.getEntry()),
				destPath.resolve(node.getPathFrom(rootNode)).toFile()
			);
		}
	}

	@Override
	public int getTargetProgress() throws IOException {
		long size = 0;
		for (ZipNode node : sourceNode.getAllChildren()){
			size += node.getEntry().getSize();
		}
		return (int) size;
	}

	@Override
	public String getTitle() {
		return String.format("Unzipping %s", sourceNode.getName());
	}

}
