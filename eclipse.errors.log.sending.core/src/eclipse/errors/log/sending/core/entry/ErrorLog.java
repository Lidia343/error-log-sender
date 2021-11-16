package eclipse.errors.log.sending.core.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.core.runtime.Platform;

public class ErrorLog extends Entry 
{
	private static final String m_bak_prefix = ".bak";
	private static final String m_extension = ".log";
	
	private File m_parentFile;
	private Queue<File> m_bakFiles;
	
	public ErrorLog(String a_entryName) 
	{
		super(a_entryName);
	}

	@Override
	public InputStream getInputStream() throws FileNotFoundException 
	{
		if (m_parentFile == null)
		{
			File logFile = Platform.getLogFileLocation().toFile();
			m_parentFile = logFile.getParentFile();
			initBakFiles();
			m_hasNext = !m_bakFiles.isEmpty();
			if (logFile.exists())
			{
				return new FileInputStream(logFile);
			}
		}
		return getNextBak();
	}
	
	protected InputStream getNextBak() throws FileNotFoundException
	{
		if (m_bakFiles == null) return null;
		File bak = m_bakFiles.poll();
		if (bak != null)
		{
			m_entryName = bak.getName();
			m_hasNext = !m_bakFiles.isEmpty();
			return new FileInputStream(bak);
		}
		return null;
	}
	
	protected void initBakFiles()
	{
		m_bakFiles = new LinkedList<>();
		for (File file : m_parentFile.listFiles())
		{
			String name = file.getName();
			if (name.startsWith(m_bak_prefix) && name.endsWith(m_extension))
			{
				m_bakFiles.add(file);
			}
		}
	}
}
