package eclipse.errors.log.sending.core;

import org.eclipse.core.runtime.Platform;

import eclipse.errors.log.sending.core.exceptions.BlockedThreadException;
import eclipse.errors.log.sending.core.system.SystemInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportArchiveCreator 
{
	private final String m_systemInfFileName = "metadata.xml";
	private String m_zipFileName = "report";
	private final String m_zipFileComment = "Archive with system information and the error log.";
	
	public ReportArchiveCreator ()
	{
		correctZipFileName();
	}
	
	private void correctZipFileName ()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		
		String day = redactTimePart(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
		String month =  redactTimePart(Integer.toString(calendar.get(Calendar.MONTH) + 1));
		String year = Integer.toString(calendar.get(Calendar.YEAR));
		
		String hour =  redactTimePart(Integer.toString(calendar.get(Calendar.HOUR)));
		String min =  redactTimePart(Integer.toString(calendar.get(Calendar.MINUTE)));
		String sec =  redactTimePart(Integer.toString(calendar.get(Calendar.SECOND)));
		
		String now = day + "-" + month + "-" + year + " - " + hour + "-" + min + "-" + sec;
		
		m_zipFileName += " (" + now + ").zip";
	}
	
	private String redactTimePart (String a_part)
	{
		if (a_part.length() == 1) a_part = "0" + a_part;
		return a_part;
	}
	
	public void createReportArchive () throws IOException, InterruptedException, BlockedThreadException
	{
		File logFile = Platform.getLogFileLocation().toFile();
		String logFilePath = logFile.getPath();
		int endIndex = logFilePath.lastIndexOf('.');
		logFilePath = logFilePath.substring(0, endIndex);
		
		try (FileInputStream logFis = new FileInputStream(logFile); FileOutputStream fout = new FileOutputStream(logFilePath + m_zipFileName);
			 ZipOutputStream zout = new ZipOutputStream(fout))
		{
			zout.setComment(m_zipFileComment);
			zout.putNextEntry(new ZipEntry(logFile.getName()));
			
			writeToOutputStream(logFis, zout);
			zout.closeEntry();
			
			File systemInfFile = new File(m_systemInfFileName);
			FileWriter systemWriter = new FileWriter(systemInfFile, false);
			
			SystemInformation systemInf = new SystemInformation();
			systemWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			systemWriter.write("<metadata>\n");
			writeXmlElement(systemWriter, "osName", systemInf.getOsName());
			writeXmlElement(systemWriter, "username", systemInf.getUsername());
			writeXmlElement(systemWriter, "ramAmount", systemInf.getRamAmount());
			writeXmlElement(systemWriter, "screenResolution", systemInf.getScreenResolution());
			systemWriter.write("</metadata>");
			systemWriter.close();
			
			zout.putNextEntry(new ZipEntry(m_systemInfFileName));
			
			FileInputStream systemFis = new FileInputStream(systemInfFile);
			writeToOutputStream(systemFis, zout);
			systemFis.close();
		}
		catch (IOException e)
		{
			throw e;
		}
	}
	
	private void writeToOutputStream (FileInputStream a_fis, ZipOutputStream a_zout) throws IOException
	{
		byte[] buffer = new byte[64*1024];
		int length = a_fis.read(buffer);
		while (length > 0)
		{
			a_zout.write(buffer, 0, length);
			length = a_fis.read(buffer);
		}
	}
	
	private void writeXmlElement (FileWriter a_writer, String a_tag, String a_entry) throws IOException
	{
		a_writer.write("\t<" + a_tag + ">");
		a_writer.write(a_entry);
		a_writer.write("</" + a_tag + ">\n");
	}
}
