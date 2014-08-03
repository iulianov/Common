package aohara.common.workflows;

import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class UnzipTask extends WorkflowTask {
	
	private final Path zipPath, destPath;
	private final Set<ZipEntry> zipEntries;
	
	public UnzipTask(Workflow workflow, Path zipPath, Path destPath, Set<ZipEntry> zipEntries){
		super(workflow);
		this.zipPath = zipPath;
		this.destPath = destPath;
		this.zipEntries = zipEntries;
	}

	@Override
	public Boolean call() throws Exception {
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			for (ZipEntry entry : zipEntries) {
				FileUtils.copyInputStreamToFile(
					zipFile.getInputStream(entry),
					destPath.resolve(entry.getName()).toFile());
			}
		}
		return true;
	}

	@Override
	protected int getTargetProgress() throws InvalidContentException {
		long size = 0;
		for (ZipEntry entry : zipEntries){
			size += entry.getSize();
		}
		return (int) size;
	}

}
