package aohara.common.downloader;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;

import aohara.common.Listenable;
import aohara.common.progressDialog.ProgressListener;

public class Downloader extends Listenable<ProgressListener<Path>> {

	private final ExecutorService executor;
	private final boolean downToTemp;
	private int downloads = 0;

	public Downloader(int numConcurrentDownloads) {
		this(numConcurrentDownloads, false);
	}

	public Downloader(int numConcurrentDownloads, boolean downToTemp) {
		this(Executors.newFixedThreadPool(numConcurrentDownloads), downToTemp);
	}

	public Downloader(ExecutorService executor, boolean downToTemp) {
		this.executor = executor;
		this.downToTemp = downToTemp;
	}

	public Future<Path> download(URL url, Path path) {
		int targetBytes;
		try {
			targetBytes = url.openConnection().getContentLength();
			return executor.submit(new DownloadTask(url, path, targetBytes));
		} catch (IOException e) {
			for (ProgressListener<Path> l : getListeners()) {
				l.progressError(path, downloads);
			}
		}
		return null;
	}

	private class DownloadTask implements Callable<Path> {

		private final URL url;
		private final Path dest;
		private final int totalBytes;
		private int currentBytes = 0;

		public DownloadTask(URL url, Path path, int totalBytes) {
			this.url = url;
			this.dest = path;
			this.totalBytes = totalBytes;
		}

		@Override
		public Path call() {
			boolean error = false;
			downloads++;

			// Notify of download start
			for (ProgressListener<Path> l : getListeners()) {
				l.progressStarted(dest, totalBytes, downloads);
			}

			Path tempPath = null;
			try {
				if (downToTemp) {
					// Download to temporary file, and then move over
					tempPath = Files.createTempFile("download", ".temp");
					download(url, tempPath);
					FileUtils.moveFile(tempPath.toFile(), dest.toFile());
				} else {
					// Download directly to destination
					download(url, dest);
				}
			} catch (IOException e) {
				if (tempPath != null && tempPath.toFile().exists()) {
					tempPath.toFile().delete();
				}
				e.printStackTrace();
				error = true;
			}

			// Notify of download result
			downloads--;
			for (ProgressListener<Path> l : getListeners()) {
				if (error) {
					l.progressError(dest, downloads);
				} else {
					l.progressComplete(dest, downloads);
				}
			}

			return dest;
		}

		/**
		 * Download URL to file, and report progress
		 * @param url
		 * @param file
		 * @throws IOException
		 */
		private void download(URL url, Path file) throws IOException {			
			try(
				BufferedInputStream in = new BufferedInputStream(url.openStream());
				FileOutputStream fout = new FileOutputStream(file.toFile());
			){
				byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1) {
					currentBytes += count;
					fout.write(data, 0, count);
					for (ProgressListener<Path> l : getListeners()){
						l.progressMade(dest, currentBytes);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	public class DownloadErrorException extends Exception {}
}
