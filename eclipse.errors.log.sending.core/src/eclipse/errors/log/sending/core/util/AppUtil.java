package eclipse.errors.log.sending.core.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipOutputStream;

public class AppUtil 
{
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
	
	public static void writeToXmlFile (FileWriter a_writer, String a_tag, String a_entry) throws IOException
	{
		a_writer.write("\t<" + a_tag + ">");
		a_writer.write(a_entry);
		a_writer.write("</" + a_tag + ">\r\n");
	}
	
	public static String getInputStreamAsString (ProcessBuilder a_processBuilder) throws IOException, InterruptedException
	{
		Process process = a_processBuilder.start();
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufReader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		
		String line;
		while ((line = bufReader.readLine()) != null)
		{
			stringBuilder.append(line + "\r\n");
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
}
