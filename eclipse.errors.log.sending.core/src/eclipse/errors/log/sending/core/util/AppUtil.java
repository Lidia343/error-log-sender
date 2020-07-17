package eclipse.errors.log.sending.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.email.EmailChecker;

public class AppUtil 
{
	public static final String PLUGIN_ID = "eclipse.errors.log.sending.core";
	
	public static boolean isDigit (char c)
	{
		String s = Character.toString(c);
		try 
		{
			Integer.parseInt(s);
		} 
		catch (NumberFormatException e) 
		{
			return false;
		}
		return true;
	}
	
	public static String getCurrentDateAndTime ()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		
		String day = addZeroToString((Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))));
		String month =  addZeroToString(Integer.toString(calendar.get(Calendar.MONTH) + 1));
		String year = Integer.toString(calendar.get(Calendar.YEAR));
		
		String hour =  addZeroToString(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
		String min =  addZeroToString(Integer.toString(calendar.get(Calendar.MINUTE)));
		String sec =  addZeroToString(Integer.toString(calendar.get(Calendar.SECOND)));
		
		return day + "-" + month + "-" + year + " - " + hour + "-" + min + "-" + sec;
	}
	
	public static String addZeroToString (String a_string)
	{
		return a_string.length() == 1 ? "0" + a_string : a_string;
	}
	
	public static String getInputStreamAsString (ProcessBuilder a_processBuilder) throws IOException, InterruptedException
	{
		Process process = a_processBuilder.start();
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufReader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		
		String line;
		while ((line = bufReader.readLine()) != null)
		{
			stringBuilder.append(line + System.lineSeparator());
		}
		process.waitFor();
		return stringBuilder.toString();
	}
	
	public static void writeBytes (OutputStream a_out, String a_string) throws IOException
	{
		for (int i = 0; i < a_string.length(); i++)
		{
			a_out.write((byte)a_string.charAt(i));
		}
	}
	
	public static void writeInputStreamToOutputStream (FileInputStream a_fis, ZipOutputStream a_zout) throws IOException
	{
		byte[] buffer = new byte[64*1024];
		int length = a_fis.read(buffer);
		while (length > 0)
		{
			a_zout.write(buffer, 0, length);
			length = a_fis.read(buffer);
		}
	}
	
	public static void writeToXmlFile (FileWriter a_writer, String a_tag, String a_entry) throws IOException
	{
		a_writer.write("\t<" + a_tag + ">");
		a_writer.write(a_entry);
		a_writer.write("</" + a_tag + ">" + System.lineSeparator());
	}
	
	public static void writeToFile (String a_filePath, String a_line) throws FileNotFoundException, IOException
	{
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter
			(new FileOutputStream(a_filePath))))
		{
			out.write(a_line);
		}
	}
	
	public static String readFromFile (String a_filePath) throws IOException
	{
		File file = new File(a_filePath);
		file.createNewFile();
		
		try (BufferedReader in = new BufferedReader (new InputStreamReader(new FileInputStream(file))))
		{
			return in.readLine();
		}
	}
	
	public static String getEmailFromPreferences ()
	{
		return getPreferences().get(EmailChecker.EMAIL_KEY, "");
	}
	
	public static boolean putEmailPreference (String a_email) throws BackingStoreException
	{
		EmailChecker emailChecker = new EmailChecker();
		if (!emailChecker.checkEmail(a_email)) return false;
		
		IEclipsePreferences preferences = getPreferences();
		preferences.put(EmailChecker.EMAIL_KEY, a_email);
		preferences.flush();
		return true;
	}
	
	private static IEclipsePreferences getPreferences ()
	{
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);//ConfigurationScope
	}
}
