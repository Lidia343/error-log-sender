package eclipse.errors.log.sending.core.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import eclipse.errors.log.sending.core.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class Client 
{
	private final String m_tokenFileName = "properties.txt";
	private String m_request;
	private String m_token;
	
	public Client () throws IOException
	{
		setRequestAndToken();
	}
	
	public void sendReportArchive (String a_reportArchivePath) throws IOException
	{
		URL requestUrl = new URL(m_request);
		HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", m_token);
		connection.connect();
		
		try(OutputStream out = connection.getOutputStream())
		{
			int beginIndex = a_reportArchivePath.lastIndexOf("\\") + 1;
			String reportArchiveName = a_reportArchivePath.substring(beginIndex);
			int archiveNameLength = reportArchiveName.length();
			String archiveNameLengthLine = Integer.toString(archiveNameLength);
			
			archiveNameLengthLine = AppUtil.addZeroToString(archiveNameLengthLine);
			AppUtil.writeBytes(out, archiveNameLengthLine);       //Запись в поток длины имени архива
			AppUtil.writeBytes(out, reportArchiveName);           //Запись в поток имени архива
			
			int entryCount = 0;
			List<Long> entrySizes = new ArrayList<>();
			try (ZipInputStream zin = new ZipInputStream(new FileInputStream(a_reportArchivePath)))
			{
				while (zin.getNextEntry() != null)
				{
					long size = 0L;
					while (zin.read() != -1)
					{
						size++;
					}
					entrySizes.add(size);
					entryCount++;
					zin.closeEntry();
				}
				
				String entryCountLine = Integer.toString(entryCount);
				entryCountLine = AppUtil.addZeroToString(entryCountLine);
				AppUtil.writeBytes(out, entryCountLine);          //Запись в поток количества вложений архива
			}
			
			try (ZipInputStream zin = new ZipInputStream(new FileInputStream(a_reportArchivePath)))
			{
				for (int i = 0; i < entryCount; i++)
				{
					ZipEntry entry = zin.getNextEntry();
					String entryName = entry.getName();
					String entryNameLenthLine = Integer.toString(entryName.length());
					
					entryNameLenthLine = AppUtil.addZeroToString(entryNameLenthLine);
					AppUtil.writeBytes(out, entryNameLenthLine);         //Запись в поток длины имени вложения
					AppUtil.writeBytes(out, entryName);                  //Запись в поток имени вложения
					
					String byteCountLine = Long.toString(entrySizes.get(i));
					String byteCountLineLength = Integer.toString(byteCountLine.length());
					
					byteCountLineLength = AppUtil.addZeroToString(byteCountLineLength);
					AppUtil.writeBytes(out, byteCountLineLength);         //Запись в поток длины строки, содержащей размер вложения
					AppUtil.writeBytes(out, byteCountLine);               //Запись в поток размера вложения (Б)
					
					byte[] buffer = new byte[1024*64];
					int length;
					while ((length = zin.read(buffer)) > 0)               //Запись в поток архива
					{
						out.write(buffer, 0, length);
					}
					zin.closeEntry();
				}
			}
			File archive = new File(a_reportArchivePath);
			archive.delete();
		}
		
		if (connection.getResponseCode() >= 400)
		{
			throw new IOException(connection.getResponseMessage());
		}
		
		connection.disconnect();
	}
	
	private void setRequestAndToken () throws IOException
	{
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(m_tokenFileName);
			     BufferedReader reader = new BufferedReader(new InputStreamReader(in)))
		{
			m_request = reader.readLine();
			m_token = reader.readLine();
			if (m_token.endsWith("\r\n")) 
				m_token = m_token.substring(0, m_token.length());
		}
	}
}
