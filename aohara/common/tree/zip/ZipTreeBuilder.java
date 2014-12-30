package aohara.common.tree.zip;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import aohara.common.tree.DirNode;
import aohara.common.tree.TreeNode;

public class ZipTreeBuilder {

	private final Path zipPath;
	private final Map<String, TreeNode> collected = new HashMap<>(); // The nodes collected so far
	private final TreeNode root = new DirNode("/"); // root node

	/**
	 * creates a new ZipFileReader from a ZipFile.
	 */
	public ZipTreeBuilder(Path zipPath) {
		this.zipPath = zipPath;
		collected.put("", root);
	}

	/**
	 * reads all entries, creates the corresponding Nodes and returns the
	 * root node.
	 */
	public TreeNode process() throws IOException {
		try(ZipFile zipFile = new ZipFile(zipPath.toFile())){
			for (ZipEntry entry : Collections.list(zipFile.entries())){
				if (!collected.containsKey(entry.getName())){
					addEntry(entry);
				}
			}
			
			return root;
		}
	}

	/**
	 * adds an entry to our tree.
	 *
	 * This may create a new ZipNode and then connects it to its parent
	 * node.
	 * 
	 * @returns the ZipNode corresponding to the entry.
	 */
	private TreeNode addEntry(ZipEntry entry) {
		Path entryPath = Paths.get(entry.getName());
		
		// Create node, and add to collection
		TreeNode node = entry.isDirectory() ? new DirNode(Paths.get(entry.getName())) : new ZipFileNode(entry);
		collected.put(entryPath.toString(), node);
		
		connectParent(node, entryPath);
		
		return node;
	}
	
	private void connectParent(TreeNode child, Path entryPath){
		Path parentPath = entryPath.getParent();
		if (parentPath == null){ // top-level node
			root.addChild(child);
		} else {  // nested node with parent that may or may not exist
			TreeNode parent = collected.get(parentPath.toString());
			if (parent == null){
				parent = new DirNode(parentPath.getFileName().toString());
				collected.put(parentPath.toString(), parent);
				connectParent(parent, parentPath);
			}
			parent.addChild(child);
		}
	}
}
