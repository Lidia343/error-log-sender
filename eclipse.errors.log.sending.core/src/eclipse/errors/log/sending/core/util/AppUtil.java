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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.email.EmailChecker;

public class AppUtil 
{
	public static final String PLUGIN_ID = "eclipse.errors.log.sending.core";
	
	public static boolean isDigit (char a_c)
	{
		String s = Character.toString(a_c);
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
	
	public static String commandAndGetResult (ProcessBuilder a_processBuilder, String a_commandLineArray[]) throws IOException, InterruptedException
	{
		a_processBuilder.command(a_commandLineArray[0], a_commandLineArray[1], a_commandLineArray[2]);
		return AppUtil.getInputStreamAsString(a_processBuilder);
	}
	
	private static String getInputStreamAsString (ProcessBuilder a_processBuilder) throws IOException, InterruptedException
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
	
	public static void writeInputStreamToOutputStream (FileInputStream a_fin, ZipOutputStream a_zout) throws IOException
	{
		byte[] buffer = new byte[64*1024];
		int length = a_fin.read(buffer);
		while (length > 0)
		{
			a_zout.write(buffer, 0, length);
			length = a_fin.read(buffer);
		}
		a_zout.closeEntry();
	}
	
	public static void writeFileToOutputStream (File a_file, ZipOutputStream a_zout) throws IOException
	{
		try (FileInputStream in = new FileInputStream(a_file))
		{
			AppUtil.writeInputStreamToOutputStream(in, a_zout);
		}
		a_file.delete();
	}
	
	public static void writeToXmlFile (FileWriter a_writer, String a_tag, String a_entry) throws IOException
	{
		a_writer.write("\t<" + a_tag + ">");
		a_writer.write(a_entry);
		a_writer.write("</" + a_tag + ">" + System.lineSeparator());
	}
	
	public static void writeToFile (String a_filePath, String a_string) throws FileNotFoundException, IOException
	{
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(a_filePath))))
		{
			out.write(a_string);
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
	
	public static File putNextEntryAndGetEntryFile (ZipOutputStream a_zout, String a_fileName) throws IOException
	{
		a_zout.putNextEntry(new ZipEntry(a_fileName));
		return new File(a_fileName);
	}
	
	public static String getEmailFromPreferences ()
	{
		return getPreferences().get(EmailChecker.EMAIL_KEY, "");
	}
	
	public static void putEmailPreference (String a_email) throws BackingStoreException
	{
		if (!new EmailChecker().checkEmail(a_email)) return;
		
		IEclipsePreferences preferences = getPreferences();
		preferences.put(EmailChecker.EMAIL_KEY, a_email);
		preferences.flush();
	}
	
	private static IEclipsePreferences getPreferences ()
	{
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);
	}
}
