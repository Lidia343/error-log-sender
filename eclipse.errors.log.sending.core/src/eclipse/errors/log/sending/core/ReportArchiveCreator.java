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
import java.util.zip.ZipOutputStream;

/**
 * Класс для создания архива с файлами, содержащими различную информацию о 
 * системе пользователя и используемом приложении.
 */
public class ReportArchiveCreator 
{
	public final String m_entryExtensionPointId = "eclipse.errors.log.sending.core.entries";
	public final String m_entryFactoryExtensionPointId = "eclipse.errors.log.sending.core.entryFactories";
	
	private String m_reportArchivePath = "report.zip";
	
	/**
	 * Создаёт архив во временной директории пользователя, а внутри архива -
	 * файлы, содержащие различные аппаратные и программные характеристики 
	 * системы пользователя (название ОС, количество установленной ОЗУ и т. д.), 
	 * конфигурацию используемого приложения и информацию о его работе (лог с 
	 * ошибками).
	 * @throws Exception 
	 */
	public void createReportArchive () throws Exception
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(m_entryExtensionPointId);
		
		m_reportArchivePath = System.getProperty("java.io.tmpdir") + File.separator + m_reportArchivePath;
		
		try (FileOutputStream logFout = new FileOutputStream(m_reportArchivePath);
			 ZipOutputStream zipOut = new ZipOutputStream(logFout))
		{
			for (IConfigurationElement e : extensions)
			{
				String bundleSymbolicName = e.getContributor().getName();
				Class<?> entryClass = Platform.getBundle(bundleSymbolicName).loadClass(e.getAttribute("class"));
				String entryName = e.getAttribute("name");
				Entry entry = ((Entry)entryClass.getConstructor(String.class).newInstance(entryName));
				InputStream input = entry.getInputStream();
				if (input != null)
				{
					AppUtil.putNextEntryAndWriteToOutputStream(zipOut, entryName, input);
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
	 * @return путь к архиву, созданному методом "createReportArchive".
	 */
	public String getReportArchivePath ()
	{
		return m_reportArchivePath;
	}
}
