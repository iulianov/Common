package aohara.common.workflows.tasks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import aohara.common.workflows.ConflictResolver;
import aohara.common.workflows.ConflictResolver.Resolution;
import aohara.common.workflows.Workflow;

public class UnzipTask extends WorkflowTask {
	
	private final Path zipPath, destPath;
	private final Map<ZipEntry, Path> zipEntries;
	private final ConflictResolver cr;
	
	public UnzipTask(
			Workflow workflow, Path zipPath, Path destPath,
			Map<ZipEntry, Path> zipEntries, ConflictResolver cr){
		super(workflow);
		this.zipPath = zipPath;
		this.destPath = destPath;
		this.zipEntries = zipEntries;
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
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			for (Entry<ZipEntry, Path> entry : zipEntries.entrySet()) {
				FileUtils.copyInputStreamToFile(
					zipFile.getInputStream(entry.getKey()),
					destPath.resolve(entry.getValue()).toFile());
			}
		}
	}

	@Override
	public int getTargetProgress() throws IOException {
		long size = 0;
		for (ZipEntry entry : zipEntries.keySet()){
			size += entry.getSize();
		}
		return (int) size;
	}

	@Override
	public String getTitle() {
		return String.format("Unzipping %s", zipPath);
	}

}
