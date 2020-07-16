package eclipse.errors.log.sending.ui;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import eclipse.errors.log.sending.core.email.EmailWorker;
import eclipse.errors.log.sending.core.email.IEmailSavingListener;
import eclipse.errors.log.sending.core.ReportArchiveCreator;
import eclipse.errors.log.sending.core.client.Client;

public class SendCommand extends AbstractHandler
{
	private final String m_messageTitle = "Отправка отчёта об ошибке";
	private final String m_successMessage = "Отчёт об ошибке успешно отправлен";
	public final static String ERROR_MESSAGE = "Произошла ошибка. Подробная информация:" + System.lineSeparator();
	
	private Shell m_parent = null;
	private Client m_client = null;
	
	@Override
	public Object execute (ExecutionEvent a_event) throws ExecutionException 
	{
		m_parent = HandlerUtil.getActiveShell(a_event);
		try 
		{
			IEmailSavingListener emailSavingListener = new IEmailSavingListener ()
			{
				@Override
				public void emailSaved() throws Exception
				{
					sendReportArchive();
				}
			};
			
			EmailWorker emailWorker = new EmailWorker();
			if (!emailWorker.checkEmailFile())
			{
				EmailWindow emailWindow = new EmailWindow(emailSavingListener);
				emailWindow.show();
			}
			else sendReportArchive();
		} 
		catch (IOException | InterruptedException e) 
		{
			MessageDialog.openError(m_parent, m_messageTitle, ERROR_MESSAGE + e.getMessage());
		}
		finally 
		{
			if (m_client != null) m_client.deleteArchive();
		}
		return null;
	}
	
	private void sendReportArchive() throws IOException, InterruptedException
	{
		ReportArchiveCreator archiveCreator = new ReportArchiveCreator();
		archiveCreator.createReportArchive();
		m_client = new Client();
		m_client.sendReportArchive(archiveCreator.getReportArchivePath());
		MessageDialog.openInformation(m_parent, m_messageTitle, m_successMessage);
	}
}
