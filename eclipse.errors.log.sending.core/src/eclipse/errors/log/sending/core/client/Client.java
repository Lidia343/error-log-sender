package eclipse.errors.log.sending.core.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipInputStream;

public class Client 
{
	private String m_token = "";
	private final String m_mainUrlPart = "";
	
	public void sendReportArchive (ZipInputStream a_zipInputStream) throws IOException
	{
		String request = m_mainUrlPart;
		String response = sendRequest(request);
		if (response == null) throw new IOException();
	}
	
	private String sendRequest (String a_url) throws IOException
	{
		URL requestUrl = new URL(a_url);
		HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization", "OAuth" + m_token);
		connection.connect();
		
		int code = connection.getResponseCode();
		if (code >= 400 && code < 500)
		{
			throw new IOException("Ошибка клиента");
		}
		if (code >= 500)
		{
			throw new IOException("Ошибка сервера");
		}
		
		try (BufferedReader reader = new BufferedReader (new InputStreamReader (connection.getInputStream())))
		{
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			{
				stringBuilder.append(line + "\n");
			}
			connection.disconnect();
			return stringBuilder.toString();
		}
	}
}
