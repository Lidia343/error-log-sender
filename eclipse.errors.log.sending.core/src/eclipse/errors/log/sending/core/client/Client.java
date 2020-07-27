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

import eclipse.errors.log.sending.core.util.AppUtil;

/**
 * Класс для отправки на сервер архива, созданного с помощью класса 
 * ReportArchiveCreator, и адреса электронной почты пользователя.
 */
public class Client 
{
	private final String m_configFileName = "config.txt";
	private String m_request;
	private String m_token;
	private String m_reportArchivePath;
	
	public Client () throws IOException
	{
		setRequestAndToken();
	}
	
	/**
	 * Отправляет на сервер длину адреса электронной почты (для удобства
	 * чтения адреса сервером), сам email (для идентификации клиента,
	 * который отправляет архив, и для возможности обратной связи), длину
	 * имени архива, имя архива, а также его содержимое.
	 * 
	 * @param a_reportArchivePath
	 *        Путь к архиву
	 * @throws IOException
	 */
	public void sendReportArchive (String a_reportArchivePath) throws IOException
	{
		m_reportArchivePath = a_reportArchivePath;
		
		HttpURLConnection connection = connect(m_request  + "file/upload");
		
		try(OutputStream out = connection.getOutputStream())
		{
			String email = AppUtil.getEmailFromPreferences();
			String emailLengthLine = AppUtil.addZeroToString(Integer.toString(email.length()));
			
			AppUtil.writeBytes(out, emailLengthLine);			  //Запись в поток длины адреса почты
			AppUtil.writeBytes(out, email);                       //Запись в поток адреса почты
			
			int beginIndex = m_reportArchivePath.lastIndexOf(File.separator) + 1;
			String reportArchiveName = m_reportArchivePath.substring(beginIndex);
			int archiveNameLength = reportArchiveName.length();
			String archiveNameLengthLine = Integer.toString(archiveNameLength);
			archiveNameLengthLine = AppUtil.addZeroToString(archiveNameLengthLine);
			
			AppUtil.writeBytes(out, archiveNameLengthLine);       //Запись в поток длины имени архива
			AppUtil.writeBytes(out, reportArchiveName);           //Запись в поток имени архива
			
			
			try (InputStream in = new FileInputStream(m_reportArchivePath))
			{
				byte[] buffer = new byte[1024*64];
				int length;
				while ((length = in.read(buffer)) > 0)            //Запись в поток архива
				{
					out.write(buffer, 0, length);
				}
			}
		}
		
		disconnect(connection);
	}
	
	/**
	 * Устанавливает адрес сервера и токен для авторизации (информация 
	 * читается из файла "config.txt", который находится в ресурсах 
	 * плигина).
	 * @throws IOException
	 */
	private void setRequestAndToken () throws IOException
	{
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(m_configFileName);
			     BufferedReader reader = new BufferedReader(new InputStreamReader(in)))
		{
			m_request = reader.readLine();
			m_token = reader.readLine();
			if (m_token.endsWith(System.lineSeparator()))
			{
				m_token = m_token.substring(0, m_token.length());
			}
		}
	}
	
	/**
	 * Устанавливает соединение с сервером.
	 * @param a_request
	 *        Запрос к серверу
	 * @return объект класса HttpURLConnection
	 * @throws IOException
	 */
	private HttpURLConnection connect (String a_request) throws IOException
	{
		URL requestUrl = new URL(a_request);
		HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", m_token);
		connection.connect();
		
		return connection;
	}
	
	/**
	 * Закрывает текущее соединение с сервером.
	 * @param a_connection
	 *        Объект класса HttpURLConnection
	 * @throws IOException
	 */
	private void disconnect (HttpURLConnection a_connection) throws IOException
	{
		if (a_connection.getResponseCode() >= 400)
		{
			throw new IOException(a_connection.getResponseMessage());
		}
		
		a_connection.disconnect();
	}
	
	/**
	 * Отправляет предыдущий и новый email на сервер.
	 * @param a_oldValue
	 *        Предыдущее значение адреса почты
	 * @param a_newValue 
	 *        Новое значение адреса почты
	 * @throws IOException
	 */
	public void sendEmail (String a_oldValue, String a_newValue) throws IOException
	{
		HttpURLConnection connection = connect(m_request  + "email");
		
		try(OutputStream out = connection.getOutputStream())
		{
			int oldValueLength = a_oldValue.length();
			String oldValueLengthLine = AppUtil.addZeroToString(Integer.toString(oldValueLength));
			
			String newValueLengthLine = AppUtil.addZeroToString(Integer.toString(a_newValue.length()));
			
			AppUtil.writeBytes(out, oldValueLengthLine);
			if (oldValueLength > 0) AppUtil.writeBytes(out, a_oldValue);
			
			AppUtil.writeBytes(out, newValueLengthLine);
			AppUtil.writeBytes(out, a_newValue);
		}
		
		disconnect(connection);
	}
	
	/**
	 * Удаляет архив. Если путь к архиву равен null (архив не был отправлен на сервер),
	 * метод ничего не делает.
	 */
	public void deleteArchive ()
	{
		if (m_reportArchivePath == null) return;
		File archive = new File(m_reportArchivePath);
		archive.delete();
	}
}
