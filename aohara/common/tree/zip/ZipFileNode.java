package aohara.common.tree.zip;

import java.nio.file.Paths;
import java.util.zip.ZipEntry;

import aohara.common.tree.FileNode;

public class ZipFileNode extends FileNode {
	
	public final ZipEntry entry;
	
	public ZipFileNode(ZipEntry entry){
		super(Paths.get(entry.getName()).getFileName().toString());
		this.entry = entry;
	}
}
