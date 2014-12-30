package aohara.common.tree;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DirNode extends TreeNode {
	
	private final Map<String, TreeNode> children = new HashMap<>();
	
	public DirNode(String name){
		super(name);
	}
	
	public DirNode(Path path){
		this(path.getFileName().toString());
	}
	
	@Override
	public boolean isDir(){
		return true;
	}
	
	@Override
	public Collection<TreeNode> getChildren(){
		return children.values();
	}
	
	public Collection<TreeNode> getAllChildren(){
		Collection<TreeNode> children = new LinkedList<>();
		for (TreeNode child : getChildren()){
			children.add(child);
			children.addAll(child.getAllChildren());
		}
		return children;
	}
	
	public TreeNode addChild(TreeNode child){
		child.parent = this;
		children.put(child.getName(), child);
		return child;
	}

	@Override
	public TreeNode getChild(String name) {
		return children.get(name);
	}
}
