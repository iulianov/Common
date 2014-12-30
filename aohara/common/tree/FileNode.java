package aohara.common.tree;

import java.util.Collection;
import java.util.LinkedList;

public class FileNode extends TreeNode {
	
	public FileNode(String name){
		super(name);
	}
	
	@Override
	public boolean isDir(){
		return false;
	}
	
	@Override
	public TreeNode addChild(TreeNode child){
		throw new RuntimeException("Cannot add children to a file node");
	}

	@Override
	public Collection<TreeNode> getChildren() {
		return new LinkedList<TreeNode>();
	}
	
	@Override
	public Collection<TreeNode> getAllChildren() {
		return getChildren();
	}

	@Override
	public TreeNode getChild(String name) {
		return null;
	}
}
