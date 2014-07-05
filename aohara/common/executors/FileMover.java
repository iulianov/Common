package aohara.common.executors;

import java.net.URL;
import java.nio.file.Path;

public class FileMover extends FileCopier {
	
	protected class MoveTask extends FileTask {

		protected MoveTask(URL input, Path dest, int totalBytes) {
			super(input, dest, totalBytes);
		}
		
		@Override
		protected void execute() throws Exception {
			super.execute();
			subject.toFile().delete();	
		}
		
	}

}
