package eclipse.errors.log.sending.core.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

/**
 * Фабрика для получения всех .bak-файлов САПРа.
 */
public class BakFileFactory implements IEntryFactory
{
	private static final String m_bakFilePrefix = ".bak";
	private static final String m_bakFileExtension = ".log";
	
	/**
	 * @return все .bak-файлы с логами для запущенной
	 * среды
	 */
	public List<Entry> getEntries () throws FileNotFoundException
	{
		List<Entry> result = new ArrayList<>();
		File logFile = Platform.getLogFileLocation().toFile();
		File parentFile = logFile.getParentFile();
		for (File file : parentFile.listFiles())
		{
			String name = file.getName();
			if (name.startsWith(m_bakFilePrefix) && name.endsWith(m_bakFileExtension))
			{
				Entry entry = new Entry(name);
				entry.setInputStream(new FileInputStream(file));
				result.add(entry);
			}
		}
		return result;
	}
}
