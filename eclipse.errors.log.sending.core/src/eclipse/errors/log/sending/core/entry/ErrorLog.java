package eclipse.errors.log.sending.core.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.Platform;

public class ErrorLog extends Entry 
{
	public ErrorLog(String a_entryName) 
	{
		super(a_entryName);
	}

	@Override
	public InputStream getInputStream() throws FileNotFoundException 
	{
		File logFile = Platform.getLogFileLocation().toFile();
		if (logFile.exists())
		{
			return new FileInputStream(logFile);
		}
		return null;
	}
}
