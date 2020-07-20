package eclipse.errors.log.sending.core.email;

import java.io.IOException;

public interface IEmailSavingListener
{
	void emailSaved () throws IOException, InterruptedException;
}
