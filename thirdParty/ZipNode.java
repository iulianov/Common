package thirdParty;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A immutable wrapper around {@link ZipEntry} allowing simple access of the
 * childs of directory entries.
 * 
 * @author Paulo Ebermann
 * @author Andrew O'Hara
 */
public class ZipNode {

	private ZipNode parent;
	private final Map<String, ZipNode> children;
	private final boolean directory;

	private final ZipEntry entry; // the corresponding Zip entry. If null, this
									// is the root entry
	public final ZipFile file; // the ZipFile from where the nodes came

	private ZipNode(ZipFile f, ZipEntry entry) {
		this.file = f;
		this.entry = entry;
		if (entry == null || entry.isDirectory()) {
			directory = true;
			children = new LinkedHashMap<String, ZipNode>();
		} else {
			directory = false;
			children = Collections.emptyMap();
		}
	}

	/**
	 * returns the last component of the name of the entry, i.e. the file name.
	 * If this is a directory node, the name ends with '/'. If this is the root
	 * node, the name is simply "/".
	 */
	public String getName() {
		if (entry == null)
			return "/";
		String longName = entry.getName();
		return longName.substring(longName.lastIndexOf('/',
				longName.length() - 2) + 1);
	}
	
	public String getPathFrom(ZipNode node){
		String thisPath = entry.getName();
		String nodePath = node.entry.getName();
		if (thisPath.startsWith(nodePath)){
			return thisPath.replaceFirst(nodePath, "");
		}
		return thisPath;
	}
	
	/**
	 * gets the corresponding ZipEntry to this node.
	 * 
	 * @return {@code null} if this is the root node (which has no corresponding
	 *         entry), else the corresponding ZipEntry.
	 */
	public ZipEntry getEntry() {
		return entry;
	}

	/**
	 * Gets the ZipFile, from where this ZipNode came.
	 * @throws IOException 
	 */
	public ZipFile reopenZipFile() throws IOException {
		return new ZipFile(file.getName());
	}

	/**
	 * returns true if this node is a directory node.
	 */
	public boolean isDirectory() {
		return directory;
	}

	/**
	 * returns this node's parent node (null, if this is the root node).
	 */
	public ZipNode getParent() {
		return parent;
	}

	/**
	 * returns an unmodifiable map of the children of this node, mapping their
	 * relative names to the ZipNode objects. (Names of subdirectories end with
	 * '/'.) The map is empty if this node is not a directory node.
	 */
	public Map<String, ZipNode> getChildren() {
		return Collections.unmodifiableMap(children);
	}
	
	/**
	 * a string representation of this ZipNode.
	 */
	public String toString() {
		return "ZipNode [" + entry.getName() + "] in [" + file.getName() + "]";
	}
	
	public Set<ZipNode> getAllChildren(){
		Set<ZipNode> children = new HashSet<>();
		for (ZipNode child : getChildren().values()){
			addChildren(children, child);
		}
		return children;
	}
	
	private void addChildren(Set<ZipNode> nodes, ZipNode node){
		nodes.add(node);
		for (ZipNode child : node.getChildren().values()){
			addChildren(nodes, child);
		}
	}

	/**
	 * creates a ZipNode tree from a ZipFile and returns the root node.
	 *
	 * The nodes' {@link #openStream()} methods are only usable until the
	 * ZipFile is closed, but the structure information remains valid.
	 */
	public static ZipNode fromZipFile(ZipFile zf) {
		return new ZipFileReader(zf).process();
	}

	/**
	 * Helper class for {@link ZipNode#fromZipFile}. It helps creating a tree of
	 * ZipNodes from a ZipFile.
	 */
	private static class ZipFileReader {

		private final ZipFile file; // The file to be read
		private final Map<String, ZipNode> collected; // The nodes collected so
														// far
		private ZipNode root; // our root node

		/**
		 * creates a new ZipFileReader from a ZipFile.
		 */
		ZipFileReader(ZipFile f) {
			this.file = f;
			this.collected = new HashMap<String, ZipNode>();
			collected.put("", root);
			root = new ZipNode(f, new ZipEntry("/"));
		}

		/**
		 * reads all entries, creates the corresponding Nodes and returns the
		 * root node.
		 */
		ZipNode process() {
			for (Enumeration<? extends ZipEntry> entries = file.entries(); entries
					.hasMoreElements();) {
				this.addEntry(entries.nextElement());
			}
			return root;
		}

		/**
		 * adds an entry to our tree.
		 *
		 * This may create a new ZipNode and then connects it to its parent
		 * node.
		 * 
		 * @returns the ZipNode corresponding to the entry.
		 */
		private ZipNode addEntry(ZipEntry entry) {
			String name = entry.getName();
			ZipNode node = collected.get(name);
			if (node != null) {
				// already in the map
				return node;
			}
			node = new ZipNode(file, entry);
			collected.put(name, node);
			this.findParent(node);
			return node;
		}

		/**
		 * makes sure that the parent of a node is in the collected-list as
		 * well, and this node is registered as a child of it. If necessary, the
		 * parent node is first created and added to the tree.
		 */
		private void findParent(ZipNode node) {
			String nodeName = node.entry.getName();
			int slashIndex = nodeName.lastIndexOf('/', nodeName.length() - 2);
			if (slashIndex < 0) {
				// top-level-node
				connectParent(root, node, nodeName);
				return;
			}
			String parentName = nodeName.substring(0, slashIndex + 1);

			// Get Parent Entry
			ZipEntry parentEntry = file.getEntry(parentName);
			parentEntry = parentEntry != null ? parentEntry : new ZipEntry(
					parentName);

			ZipNode parent = addEntry(parentEntry);
			connectParent(parent, node, nodeName.substring(slashIndex + 1));
		}

		/**
		 * connects a parent node with its child node.
		 */
		private void connectParent(ZipNode parent, ZipNode child,
				String childName) {
			child.parent = parent;
			parent.children.put(childName, child);
		}
	}
}