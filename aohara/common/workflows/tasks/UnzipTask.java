package aohara.common.workflows.tasks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import aohara.common.tree.TreeNode;
import aohara.common.tree.zip.ZipFileNode;
import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.ConflictResolver.Resolution;

/**
 * WorkflowTask to extract selected files from a Zip File.
 * 
 * @author Andrew O'Hara
 */
public class UnzipTask extends WorkflowTask {
	
	private final Path zipPath;
	private final Path destPath;
	private final TreeNode sourceNode;
	private final ConflictResolver cr;
	
	public UnzipTask(Path zipPath, Path destPath, TreeNode node, ConflictResolver cr){
		super(String.format("Unzipping %s", node.getName()));
		this.zipPath = zipPath;
		this.destPath = destPath;
		this.sourceNode = node;
		node.makeRoot();
		this.cr = cr;
	}
	
	@Override
	public boolean execute() throws Exception {
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
		try(ZipFile file = new ZipFile(zipPath.toFile())){
			//TreeNode rootNode = sourceNode.getParent() != null ? sourceNode.getParent() : sourceNode;
			//unzip(workflow, file, sourceNode, rootNode);
			unzip(file, sourceNode);
			for (TreeNode child : sourceNode.getAllChildren()){
				if (child instanceof ZipFileNode){
					//unzip(workflow, file, (ZipFileNode) child, rootNode);
					unzip(file, (ZipFileNode) child);
				}
				
			}
		}
	}
	
	//private void unzip(Workflow workflow, ZipFile file, TreeNode node, TreeNode rootNode) throws IOException{
	private void unzip(ZipFile file, TreeNode node) throws IOException{
		if (node instanceof ZipFileNode){
			ZipEntry entry = ((ZipFileNode)node).entry;
			FileUtils.copyInputStreamToFile(
				file.getInputStream(entry),
				destPath.resolve(node.getPath()).toFile()
			);
			progress((int) entry.getSize());
		}
	}

	@Override
	protected int findTargetProgress() throws IOException {
		long size = 0;
		for (TreeNode node : sourceNode.getAllChildren()){
			if (node instanceof ZipFileNode){
				size += ((ZipFileNode)node).entry.getSize();
			}
		}
		return (int) size;
	}
}
