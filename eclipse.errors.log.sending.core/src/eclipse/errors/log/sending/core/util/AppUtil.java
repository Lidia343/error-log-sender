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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.email.EmailChecker;

/**
 * Утилитарный класс.
 */
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
	
	/**
	 * Устанавливает началом строки a_string символ "0", если длина строки равна 1.
	 * @param a_string 
	 *        Строка для изменения
	 * @return изменённую строку
	 */
	public static String addZeroToString (String a_string)
	{
		return a_string.length() == 1 ? "0" + a_string : a_string;
	}
	
	/**
	 * Выполняет команду для командной строки и возвращает её результат.
	 * @param a_processBuilder
	 *        Объект класса ProcessBuilder, необходимый для выполнения команды
	 * @param a_commandLineArray
	 *        Массив, содержащий команду и её аргументы
	 * @return строку, полученную в результате выполнения команды
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String commandAndGetResult (ProcessBuilder a_processBuilder, String a_commandLineArray[]) throws IOException, InterruptedException
	{
		a_processBuilder.command(a_commandLineArray[0], a_commandLineArray[1], a_commandLineArray[2]);
		return AppUtil.getInputStreamAsString(a_processBuilder);
	}
	
	/**
	 * Возвращает в виде строки входной поток процесса, полученного с помощью метода
	 * a_processBuilder.start().
	 * @param a_processBuilder
	 *        Объект класса ProcessBuilder
	 * @return входной поток процесса в виде строки
	 * @throws IOException
	 * @throws InterruptedException
	 */
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
	
	/**
	 * Записывает в выходной поток a_out все байты строки a_string.
	 * @param a_out
	 *        Выходной поток
	 * @param a_string
	 *        Строка для записи в поток
	 * @throws IOException
	 */
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
		int length;
		while ((length = a_fin.read(buffer)) > 0)
		{
			a_zout.write(buffer, 0, length);
		}
		a_zout.closeEntry();
	}
	
	/**
	 * Записывает файл a_file в выходной поток a_zout, а затем удаляет файл.
	 * @param a_file
	 * 		  Файл лдя записи
	 * @param a_zout
	 *        Выходной поток
	 * @throws IOException
	 */
	public static void writeFileToOutputStreamAndDelete (File a_file, ZipOutputStream a_zout) throws IOException
	{
		try (FileInputStream in = new FileInputStream(a_file))
		{
			AppUtil.writeInputStreamToOutputStream(in, a_zout);
		}
		a_file.delete();
	}
	
	/**
	 * Записывает один тег в xml-файл.
	 * @param a_writer
	 * 		  Объект класса FileWriter, с помощью которого производится запись в файл
	 * @param a_tagName
	 * 		  Имя тега 
	 * @param a_tagContent
	 *        Содержимое тега
	 * @throws IOException
	 */
	public static void writeToXmlFile (FileWriter a_writer, String a_tagName, String a_tagContent) throws IOException
	{
		a_writer.write("\t<" + a_tagName + ">");
		a_writer.write(a_tagContent);
		a_writer.write("</" + a_tagName + ">" + System.lineSeparator());
	}
	
	/**
	 * Записывает строку a_string в файл.
	 * @param a_filePath
	 * 		  Путь к файлу
	 * @param a_string
	 * 	      Строка для записи
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeToFile (String a_filePath, String a_string) throws FileNotFoundException, IOException
	{
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(a_filePath))))
		{
			out.write(a_string);
		}
	}
	
	/**
	 * Считывает и возвращает первую строку в файле.
	 * @param a_filePath
	 * 		  Путь к файлу
	 * @return первую строку в файле
	 * @throws IOException
	 */
	public static String readFromFile (String a_filePath) throws IOException
	{
		File file = new File(a_filePath);
		file.createNewFile();
		
		try (BufferedReader in = new BufferedReader (new InputStreamReader(new FileInputStream(file))))
		{
			return in.readLine();
		}
	}
	
	/**
	 * Создаёт в выходном потоке a_zout вложение с именем a_fileName и возвращает вложение
	 * в виде объекта класса File.
	 * @param a_zout
	 * 		  Выходной поток архива
	 * @param a_fileName
	 * 		  Имя вложения
	 * @return вложение в виде объекта класса File
	 * @throws IOException
	 */
	public static File putNextEntryAndGetEntryFile (ZipOutputStream a_zout, String a_fileName) throws IOException
	{
		a_zout.putNextEntry(new ZipEntry(a_fileName));
		return new File(a_fileName);
	}
	
	/**
	 * @return email из настроек. Если email не найден, возвращает пустую строку
	 */
	public static String getEmailFromPreferences ()
	{
		return getPreferences().get(EmailChecker.EMAIL_KEY, "");
	}
	
	/**
	 * Записывает значение a_email в настройки, если a_email соответствует формату
	 * адреса электронной почты (иначе метод ничего не делает).
	 * @param a_email
	 * 	      Адрес электронной почты для записи
	 * @throws BackingStoreException
	 */
	public static void putEmailPreference (String a_email) throws BackingStoreException
	{
		if (!new EmailChecker().checkEmail(a_email)) return;
		
		IEclipsePreferences preferences = getPreferences();
		preferences.put(EmailChecker.EMAIL_KEY, a_email);
		preferences.flush();
	}
	
	/**
	 * @return настройки IEclipsePreferences для данного плагина или null,
	 * если плагин не может быть определён
	 */
	private static IEclipsePreferences getPreferences ()
	{
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);
	}
}
