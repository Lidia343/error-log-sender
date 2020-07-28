package eclipse.errors.log.sending.core.entry;
import java.io.InputStream;

public abstract class Entry
{
	private String m_entryName;
	
	public Entry (String a_entryName)
	{
		m_entryName = a_entryName;
	}
	
	public InputStream getInputStream () throws Exception
	{
		return null;
	}
	
	public String getEntryName ()
	{
		return m_entryName;
	}
}
