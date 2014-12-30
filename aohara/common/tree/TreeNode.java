package aohara.common.tree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public abstract class TreeNode {
	
	private final String name;
	protected TreeNode parent;
	
	public TreeNode(String name){
		this.name = name;
	}
	
	public final String getName(){
		return name;
	}
	
	public final TreeNode getParent(){
		return parent;
	}
	
	public Path getPath(){
		if (parent == null || parent.getName().equals("/")){
			return Paths.get(getName());
		}
		return parent.getPath().resolve(getName());
	}
	
	public void makeRoot(){
		parent = null;
	}
	
	@Override
	public boolean equals(Object o){
		return (o instanceof TreeNode) && ((TreeNode)o).getPath().equals(getPath());
	}
	
	@Override
	public int hashCode(){
		return getPath().hashCode();
	}
	
	@Override
	public String toString(){
		return String.format("Node: %s", getPath());
	}
	
	public abstract boolean isDir();
	public abstract Collection<TreeNode> getChildren();
	public abstract Collection<TreeNode> getAllChildren();
	public abstract TreeNode addChild(TreeNode child);
	public abstract TreeNode getChild(String name);
}
