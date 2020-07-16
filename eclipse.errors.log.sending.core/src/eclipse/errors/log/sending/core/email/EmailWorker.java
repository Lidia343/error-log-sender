package eclipse.errors.log.sending.core.email;

import java.io.File;
import java.io.IOException;

import eclipse.errors.log.sending.core.util.AppUtil;

public class EmailWorker 
{
	public final static String EMAIL_FILE_PATH_PART = System.getProperty("java.io.tmpdir") + File.separator + "eclipse-report-plugin";
	public final static String EMAIL_FILE_PATH = EMAIL_FILE_PATH_PART + File.separator + "userEmail.txt";
	
	public boolean checkEmailFile () throws IOException
	{
		File userDir = new File(EMAIL_FILE_PATH_PART);
		userDir.mkdir();
		
		String line = AppUtil.readFromFile(EMAIL_FILE_PATH);
		if (!isEmail(line)) return false;
		
		return true;
	}
	
	public boolean isEmail (String a_email)
	{
		if (a_email == null) return false;
		
		int end = a_email.indexOf(File.separator);
		if (end != -1) a_email = a_email.substring(0, end);
		
		int atCharIndex = a_email.indexOf('@');
		if (atCharIndex == -1 || atCharIndex == 0 || atCharIndex >= (a_email.length() - 3)) return false;
		
		int dotCharCount = 0;
		for (int i = atCharIndex + 1; i < a_email.length(); i++)
		{
			if (a_email.charAt(i) == '.') dotCharCount++;
		}
		if (dotCharCount != 1) return false;
		
		int dotCharIndex = a_email.lastIndexOf('.');
		if (dotCharIndex == (atCharIndex + 1) || dotCharIndex == (a_email.length() - 1)) return false;
		
		return true;
	}
}
