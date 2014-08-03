package aohara.common.workflows;

import java.nio.file.Path;

public abstract class ConflictResolver {
	
	public enum Resolution { Skip, Overwrite };

	public abstract Resolution getResolution(Path conflictPath);
}
