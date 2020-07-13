package eclipse.errors.log.sending.core;

import org.eclipse.core.runtime.Platform;

import eclipse.errors.log.sending.core.system.SystemInformation;
import eclipse.errors.log.sending.core.util.AppUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportArchiveCreator 
{
	private final String m_systemInfFileName = "metadata.xml";
	private String m_reportArchivePath = "report";
	
	public ReportArchiveCreator ()
	{
		m_reportArchivePath += " (" + AppUtil.getCurrentDateAndTime() + ").zip";
	}
	
	public void createReportArchive () throws IOException, InterruptedException
	{
		File logFile = Platform.getLogFileLocation().toFile();
		String logFilePath = logFile.getPath();
		int endIndex = logFilePath.lastIndexOf('.');
		logFilePath = logFilePath.substring(0, endIndex);
		m_reportArchivePath = logFilePath + m_reportArchivePath;
		
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
		}
		catch (IOException e)
		{
			throw e;
		}
	}
	
	public String getReportArchiveName ()
	{
		return m_reportArchivePath;
	}
}
