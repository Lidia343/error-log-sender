package eclipse.errors.log.sending.core.entry;
import java.io.InputStream;
import java.util.Objects;

/**
 * Вложение архива.
 */
public class Entry
{
	protected String m_entryName;
	private InputStream m_in;
	
	public Entry (String a_entryName)
	{
		m_entryName = Objects.requireNonNull(a_entryName);
	}
	
	public void setInputStream (InputStream a_in)
	{
		m_in = Objects.requireNonNull(a_in);
	}
	
	public InputStream getInputStream () throws Exception
	{
		return m_in;
	}
	
	public String getEntryName ()
	{
		return m_entryName;
	}
}