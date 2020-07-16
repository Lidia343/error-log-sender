package eclipse.errors.log.sending.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class EmailWorker 
{
	private final String m_emailFileName = "userInfo.txt";
	private final String m_emailFilePath = System.getProperty("user.home") + File.separator + "eclipse-report-plugin";
	
	public boolean checkEmailFile () throws IOException
	{
		File userDir = new File(m_emailFilePath);
		userDir.mkdir();
		
		File emailFile = new File(m_emailFilePath + File.separator + m_emailFileName);
		emailFile.createNewFile();
		
		try (BufferedReader in = new BufferedReader (new InputStreamReader(new FileInputStream(emailFile))))
		{
			String line = in.readLine();
			if (line == null || (!isEmail(line) && !line.equals("?"))) return false;
		}
		return true;
	}
	
	public boolean isEmail (String a_email)
	{
		int end = a_email.indexOf(File.separator);
		if (end != -1) a_email = a_email.substring(0, end);
		
		int atCharIndex = a_email.indexOf('@');
		if (atCharIndex == 0 || atCharIndex >= (a_email.length() - 3)) return false;
		
		int dotCharCount = 0;
		for (int i = atCharIndex + 1; i < a_email.length(); i++)
		{
			if (a_email.charAt(i) == '.') dotCharCount++;
		}
		if (dotCharCount > 1) return false;
		
		int dotCharIndex = a_email.lastIndexOf('.');
		if (dotCharIndex < atCharIndex) return false;
		
		return true;
	}
	
	public void writeInEmailFile (String a_line) throws FileNotFoundException, IOException
	{
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter
			(new FileOutputStream(m_emailFilePath + File.separator + m_emailFileName))))
		{
			out.write(a_line);
		}
	}
}
