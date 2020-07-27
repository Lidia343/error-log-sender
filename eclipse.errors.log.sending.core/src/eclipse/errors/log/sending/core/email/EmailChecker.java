package eclipse.errors.log.sending.core.email;

import java.io.File;

import eclipse.errors.log.sending.core.util.AppUtil;

/**
 * Класс для проверки адреса электронной почты пользователя.
 */
public class EmailChecker 
{
	public static final String EMAIL_KEY = "EMAIL";
	public static final int EMAIL_TEXT_LIMIT = 99;
	
	/**
	 * Проверяет наличие адреса почты в настройках.
	 * @return true - если в настройках указан email,
	 * false - иначе
	 */
	public boolean checkEmailPreference ()
	{
		if (!checkEmail(AppUtil.getEmailFromPreferences())) return false;
		return true;
	}
	
	/**
	 * Проверяет переданную строку на соответствие формату адреса электронной почты.
	 * @param a_email 
	 *        Строка для проверки
	 * @return true - если строка прошла проверку,
	 * false - иначе
	 */
	public boolean checkEmail (String a_email)
	{
		if (a_email == null) return false;
		
		if (a_email.contains(File.separator) || a_email.contains("/") || a_email.contains("\\") ||
			a_email.contains(":") || a_email.contains("*") || a_email.contains("?") ||
			a_email.contains("\"") || a_email.contains("<") || a_email.contains(">") || a_email.contains("|")) 
		{
			return false;
		}
		
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
