package eclipse.errors.log.sending.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.ConfigurationInfo;

import eclipse.errors.log.sending.core.system.SystemInformation;
import eclipse.errors.log.sending.core.util.AppUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Класс для создания архива с файлами, содержащими различную информацию о 
 * системе пользователя и используемом приложении.
 */
@SuppressWarnings("restriction")
public class ReportArchiveCreator 
{
	private final String m_systemInfFileName = "metadata.xml";
	private final String m_summaryFileName = "summary.txt";
	private String m_reportArchivePath = "report.zip";
	
	/**
	 * Создаёт архив во временной директории пользователя, а внутри архива -
	 * файлы, содержащие различные аппаратные и программные характеристики 
	 * системы пользователя (название ОС, количество установленной ОЗУ и т. д.), 
	 * конфигурацию используемого приложения и информацию о его работе (лог с 
	 * ошибками).
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void createReportArchive () throws InterruptedException, IOException
	{
		File logFile = Platform.getLogFileLocation().toFile();
		m_reportArchivePath = System.getProperty("java.io.tmpdir") + File.separator + m_reportArchivePath;
		
		try (FileOutputStream logFout = new FileOutputStream(m_reportArchivePath);
			 ZipOutputStream zipOut = new ZipOutputStream(logFout))
		{
			if (logFile.exists())
			{
				try (FileInputStream logFin = new FileInputStream(logFile))
				{
					zipOut.putNextEntry(new ZipEntry(logFile.getName()));
					AppUtil.writeInputStreamToOutputStream(logFin, zipOut);
				}
			}
			
			File systemInfFile = AppUtil.putNextEntryAndGetEntryFile(zipOut, m_systemInfFileName);
			try (FileWriter systemInfFileWriter = new FileWriter(systemInfFile, false))
			{
				SystemInformation systemInf = new SystemInformation();
				systemInfFileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
				systemInfFileWriter.write("<metadata>" + System.lineSeparator());
				AppUtil.writeToXmlFile(systemInfFileWriter, "osName", systemInf.getOsName());
				AppUtil.writeToXmlFile(systemInfFileWriter, "username", systemInf.getUsername());
				
				String ramAmount = systemInf.getRamAmount();
				if (ramAmount != null)
				{
					AppUtil.writeToXmlFile(systemInfFileWriter, "ramAmount", systemInf.getRamAmount());
				}
				
				AppUtil.writeToXmlFile(systemInfFileWriter, "screenResolution", systemInf.getScreenResolution());
				systemInfFileWriter.write("</metadata>");
			}
			AppUtil.writeFileToOutputStreamAndDelete(systemInfFile, zipOut);
			
			File summaryFile = AppUtil.putNextEntryAndGetEntryFile(zipOut, m_summaryFileName);
			try (FileWriter summaryFileWriter = new FileWriter(summaryFile, false))
			{
				summaryFileWriter.write(ConfigurationInfo.getSystemSummary());
			}
			AppUtil.writeFileToOutputStreamAndDelete(summaryFile, zipOut);
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
