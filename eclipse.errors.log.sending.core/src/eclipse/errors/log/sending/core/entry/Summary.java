package eclipse.errors.log.sending.core.entry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.eclipse.ui.internal.ConfigurationInfo;

/**
 * Конфигурация запуска САПРа.
 */
@SuppressWarnings("restriction")
public class Summary extends Entry 
{
	public Summary(String a_entryName) 
	{
		super(a_entryName);
	}

	/**
	 * @return входной поток строки (UTF-8), содержащей информацию
	 * о конфигурации запуска САПРа (строка эквивалентна значению,
	 * возвращаемому методом ConfigurationInfo.getSystemSummary())
	 */
	@Override
	public InputStream getInputStream() throws IOException 
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8))
		{
			writer.write(ConfigurationInfo.getSystemSummary());
		}
		
		try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray()))
		{
			return in;
		}
	}
}
