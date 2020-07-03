package eclipse.errors.log.sending.core;

import org.eclipse.core.runtime.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportArchiveCreator 
{
	private final String m_zipFileName = "report.zip";
	private final String m_zipFileComment = "Archive with error log.";
	
	public void createReportArchive () throws IOException
	{
		createErrorLogInArchive();
		createSystemInformationInArchive();
	}
	
	private void createErrorLogInArchive () throws IOException
	{
		File logFile = Platform.getLogFileLocation().toFile();
		String logFilePath = logFile.getPath();
		int endIndex = logFilePath.lastIndexOf('.');
		logFilePath = logFilePath.substring(0, endIndex);
		
		try (FileInputStream fis = new FileInputStream(logFile); FileOutputStream fout = new FileOutputStream(logFilePath + m_zipFileName); 
			ZipOutputStream zout = new ZipOutputStream(fout))
		{
			zout.setComment(m_zipFileComment);
			zout.putNextEntry(new ZipEntry(logFile.getName()));
			
			byte[] buffer = new byte[64*1024];
			int length = fis.read(buffer);
			while (length > 0)
			{
				zout.write(buffer, 0, length);
				length = fis.read(buffer);
			}
			//В конце удалить архив
		}
		catch (IOException e)
		{
			throw e;
		}
	}
	
	private void createSystemInformationInArchive ()
	{
		
	}
}
