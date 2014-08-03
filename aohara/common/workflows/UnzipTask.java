package aohara.common.workflows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import aohara.common.workflows.ConflictResolver.Resolution;

public class UnzipTask extends WorkflowTask {
	
	private final Path zipPath, destPath;
	private final Set<ZipEntry> zipEntries;
	private final ConflictResolver cr;
	
	public UnzipTask(
			Workflow workflow, Path zipPath, Path destPath,
			Set<ZipEntry> zipEntries, ConflictResolver cr){
		super(workflow);
		this.zipPath = zipPath;
		this.destPath = destPath;
		this.zipEntries = zipEntries;
		this.cr = cr;
	}

	@Override
	public Boolean call() throws Exception {
		if (destPath.toFile().exists()){
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
			for (ZipEntry entry : zipEntries) {
				FileUtils.copyInputStreamToFile(
					zipFile.getInputStream(entry),
					destPath.resolve(entry.getName()).toFile());
			}
		}
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
