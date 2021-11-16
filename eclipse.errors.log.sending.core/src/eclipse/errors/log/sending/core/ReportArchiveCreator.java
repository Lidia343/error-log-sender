package eclipse.errors.log.sending.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import eclipse.errors.log.sending.core.entry.Entry;
import eclipse.errors.log.sending.core.entry.IEntryFactory;
import eclipse.errors.log.sending.core.util.AppUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

/**
 * Класс для создания архива с файлами, содержащими различную информацию о 
 * системе пользователя и используемом приложении.
 */
public class ReportArchiveCreator 
{
	public static final String ARCHIVE_NAME = "report";
	
	public final String m_entryExtensionPointId = "eclipse.errors.log.sending.core.entries";
	public final String m_entryFactoryExtensionPointId = "eclipse.errors.log.sending.core.entryFactories";
	
	private String m_reportArchivePath;
	
	/**
	 * Создаёт архив с файлами, содержащими информацию о различных
	 * аппаратных и программных характеристиках системы пользователя
	 * (названии ОС, количестве установленной ОП и т.д.), конфигурацию
	 * используемого приложения и его лог с ошибками.
	 * Архив будет создан по пути a_path.
	 * @param a_path
	 * 		  Абсолютный путь к архиву
	 * @throws Exception
	 */
	public void createReportArchive (String a_path) throws Exception
	{
		m_reportArchivePath = Objects.requireNonNull(a_path);
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(m_entryExtensionPointId);
		
		try (FileOutputStream logFout = new FileOutputStream(m_reportArchivePath);
			 ZipOutputStream zipOut = new ZipOutputStream(logFout))
		{
			for (IConfigurationElement e : extensions)
			{
				String bundleSymbolicName = e.getContributor().getName();
				Class<?> entryClass = Platform.getBundle(bundleSymbolicName).loadClass(e.getAttribute("class"));
				String entryName = e.getAttribute("name");
				Entry entry = ((Entry)entryClass.getConstructor(String.class).newInstance(entryName));
				InputStream input;
				while ((input = entry.getInputStream()) != null)
				{
					AppUtil.putNextEntryAndWriteToOutputStream(zipOut, entry.getEntryName(), input);
					if (!entry.hasNext()) break;
				}
			}
			
			extensions = registry.getConfigurationElementsFor(m_entryFactoryExtensionPointId);
			for (IConfigurationElement e : extensions)
			{
				String bundleSymbolicName = e.getContributor().getName();
				Class<?> entryFactoryClass = Platform.getBundle(bundleSymbolicName).loadClass(e.getAttribute("class"));
				IEntryFactory entryFactory = ((IEntryFactory)entryFactoryClass.getConstructor().newInstance());
				for (Entry entry : entryFactory.getEntries())
				{
					AppUtil.putNextEntryAndWriteToOutputStream(zipOut, entry.getEntryName(), entry.getInputStream());
				}
			}
		}
	}
	
	/**
	 * Создаёт архив с файлами, содержащими информацию о различных
	 * аппаратных и программных характеристиках системы пользователя
	 * (названии ОС, количестве установленной ОП и т.д.), конфигурацию
	 * используемого приложения и его лог с ошибками.
	 * Архив будет создан по пути "/temp/report.zip".
	 * @throws Exception
	 */
	public void createReportArchive () throws Exception
	{
		createReportArchive(System.getProperty("java.io.tmpdir") + File.separator + ARCHIVE_NAME + ".zip");
	}
	
	/**
	 * @return путь к архиву, создаваемому методом "createReportArchive".
	 */
	public String getReportArchivePath ()
	{
		return m_reportArchivePath;
	}
}
