package eclipse.errors.log.sending.ui.command;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.core.email.IEmailSavingListener;
import eclipse.errors.log.sending.ui.email.EmailWindow;
import eclipse.errors.log.sending.core.ReportArchiveCreator;
import eclipse.errors.log.sending.core.client.Client;

public class SendCommand extends AbstractHandler
{
	private final String m_messageTitle = "Отправка отчёта об ошибке";
	
	private Shell m_parent = null;
	private Client m_client = null;
	
	@Override
	public Object execute (ExecutionEvent a_event) throws ExecutionException 
	{
		m_parent = HandlerUtil.getActiveShell(a_event);
		try 
		{
			if (!new EmailChecker().checkEmailPreference()) new EmailWindow(getEmailSavingListener()).show();
			else sendReportArchive();
		} 
		catch (Exception e) 
		{
			MessageDialog.openError(m_parent, m_messageTitle, "Произошла ошибка. Подробная информация:" + System.lineSeparator() + e.getMessage());
		}
		finally 
		{
			if (m_client != null) m_client.deleteArchive();
		}
		return null;
	}
	
	private IEmailSavingListener getEmailSavingListener ()
	{
		return new IEmailSavingListener ()
		{
			@Override
			public void emailSaved() throws Exception
			{
				sendReportArchive();
			}
		};
	}
	
	private void sendReportArchive () throws IOException, InterruptedException
	{
		ReportArchiveCreator archiveCreator = new ReportArchiveCreator();
		archiveCreator.createReportArchive();
		m_client = new Client();
		m_client.sendReportArchive(archiveCreator.getReportArchivePath());
		MessageDialog.openInformation(m_parent, m_messageTitle, "Отчёт об ошибке успешно отправлен");
	}
}
