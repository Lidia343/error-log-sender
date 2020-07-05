package eclipse.errors.log.sending.core.exceptions;

public class BlockedThreadException extends Exception 
{
	private static final long serialVersionUID = 1L;
	private static final String m_message = "The calling thread has been blocked.";
	
	public BlockedThreadException ()
	{
		super(m_message);
	}
}
