package eclipse.errors.log.sending.core.entry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import eclipse.errors.log.sending.core.system.SystemInformation;
import eclipse.errors.log.sending.core.util.AppUtil;

/**
 * Информация о системе пользователя.
 */
public class Metadata extends Entry
{
	public Metadata(String a_entryName) 
	{
		super(a_entryName);
	}

	/**
	 * @return информацию о системе пользователя (имя ОС, пользователя,
	 * количество ОП, разрешение экрана) в виде входного потока xml-файла
	 */
	@Override
	public InputStream getInputStream() throws IOException, InterruptedException 
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
		{
			SystemInformation systemInf = new SystemInformation();
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
			writer.write("<metadata>" + System.lineSeparator());
			AppUtil.writeToXmlFile(writer, "osName", systemInf.getOsName());
			AppUtil.writeToXmlFile(writer, "username", systemInf.getUsername());
			
			String ramAmount = systemInf.getRamAmount();
			if (ramAmount != null)
			{
				AppUtil.writeToXmlFile(writer, "ramAmount", systemInf.getRamAmount());
			}
			
			AppUtil.writeToXmlFile(writer, "screenResolution", systemInf.getScreenResolution());
			writer.write("</metadata>");
		}
		
		try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray()))
		{
			return in;
		}
	}
}
