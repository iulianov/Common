package aohara.common.workflows.tasks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

class UnzipTask extends WorkflowTask {
	
	private final Map<Path, ZipEntry> zipPaths;
	private final Path zipFilePath, destFolder;

	UnzipTask(Map<Path, ZipEntry> zipPaths, Path zipFilePath, Path destFolder) {
		super("Unzipping to " + destFolder);
		this.zipPaths = zipPaths;
		this.zipFilePath = zipFilePath;
		this.destFolder = destFolder;
	}

	@Override
	public boolean execute() throws IOException {
		try(ZipFile file = new ZipFile(zipFilePath.toFile())){
			for (Path zipPath : zipPaths.keySet()){
				ZipEntry entry = zipPaths.get(zipPath);
				if (entry != null && !entry.isDirectory()){
					FileUtils.copyInputStreamToFile(
						file.getInputStream(zipPaths.get(zipPath)),
						destFolder.resolve(zipPath).toFile()
					);
				}
			}
			return true;
		}
	}

	@Override
	protected int findTargetProgress() throws IOException {
		return zipPaths.size();
	}

}
