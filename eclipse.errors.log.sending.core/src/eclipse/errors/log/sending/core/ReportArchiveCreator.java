package eclipse.errors.log.sending.core;

import org.eclipse.core.runtime.Platform;

import eclipse.errors.log.sending.core.system.SystemInformation;
import eclipse.errors.log.sending.core.util.AppUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportArchiveCreator 
{
	private final String m_systemInfFileName = "metadata.xml";
	private final String m_systemPropFileName = "sysprop.txt";
	private String m_reportArchivePath = "report";
	
	public ReportArchiveCreator ()
	{
		m_reportArchivePath += " (" + AppUtil.getCurrentDateAndTime() + ").zip";
	}
	
	public void createReportArchive () throws IOException, InterruptedException
	{
		File logFile = Platform.getLogFileLocation().toFile();
		m_reportArchivePath = System.getProperty("java.io.tmpdir") + File.separator + m_reportArchivePath;
		
		try (FileInputStream logFin = new FileInputStream(logFile); FileOutputStream logFout = new FileOutputStream(m_reportArchivePath);
			 ZipOutputStream zipOut = new ZipOutputStream(logFout))
		{
			zipOut.putNextEntry(new ZipEntry(logFile.getName()));
			
			AppUtil.writeInputStreamToOutputStream(logFin, zipOut);
			zipOut.closeEntry();
			
			File systemInfFile = new File(m_systemInfFileName);
			
			try (FileWriter systemInfFileWriter = new FileWriter(systemInfFile, false))
			{
				SystemInformation systemInf = new SystemInformation();
				systemInfFileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
				systemInfFileWriter.write("<metadata>" + System.lineSeparator());
				AppUtil.writeToXmlFile(systemInfFileWriter, "osName", systemInf.getOsName());
				AppUtil.writeToXmlFile(systemInfFileWriter, "username", systemInf.getUsername());
				
				String ramAmount = systemInf.getRamAmount();
				if (ramAmount != null)
					AppUtil.writeToXmlFile(systemInfFileWriter, "ramAmount", systemInf.getRamAmount());
				
				AppUtil.writeToXmlFile(systemInfFileWriter, "screenResolution", systemInf.getScreenResolution());
				systemInfFileWriter.write("</metadata>");
			}
			
			zipOut.putNextEntry(new ZipEntry(m_systemInfFileName));
			
			try (FileInputStream systemInfFin = new FileInputStream(systemInfFile))
			{
				AppUtil.writeInputStreamToOutputStream(systemInfFin, zipOut);
			}
			
			zipOut.closeEntry();
			
			File systemPropFile = new File(m_systemPropFileName);
			
			try (FileWriter systemPropFileWriter = new FileWriter(systemPropFile, false))
			{
				Properties properties = System.getProperties();
				for (Object key : properties.keySet())
				{
					systemPropFileWriter.write(((String)key + "=" + properties.getProperty((String)key)) + System.lineSeparator());
				}
			}
			
			zipOut.putNextEntry(new ZipEntry(m_systemPropFileName));
			
			try (FileInputStream systemPropFin = new FileInputStream(systemPropFile))
			{
				AppUtil.writeInputStreamToOutputStream(systemPropFin, zipOut);
			}
			
			zipOut.closeEntry();
		}
		catch (IOException e)
		{
			throw e;
		}
	}
	
	public String getReportArchivePath ()
	{
		return m_reportArchivePath;
	}
}
