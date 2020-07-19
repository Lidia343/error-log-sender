package eclipse.errors.log.sending.core.email;

import java.io.IOException;

import eclipse.errors.log.sending.core.util.AppUtil;

public class EmailChecker 
{
	public static final String EMAIL_KEY = "EMAIL";
	public static final int EMAIL_TEXT_LIMIT = 99;
	
	public boolean checkEmailPreference () throws IOException
	{
		if (!checkEmail(AppUtil.getEmailFromPreferences())) return false;
		return true;
	}
	
	public boolean checkEmail (String a_email)
	{	
		if (a_email == null) return false;
		
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
