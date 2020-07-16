package eclipse.errors.log.sending.ui;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import eclipse.errors.log.sending.core.EmailWorker;
import eclipse.errors.log.sending.core.ReportArchiveCreator;
import eclipse.errors.log.sending.core.client.Client;

public class SendCommand extends AbstractHandler
{
	private final String m_messageTitle = "Отправка отчёта об ошибке";
	private final String m_successMessage = "Отчёт об ошибке успешно отправлен";
	private final String m_errorMessage = "Произошла ошибка. Подробная информация:" + System.lineSeparator();
	
	@Override
	public Object execute (ExecutionEvent a_event) throws ExecutionException 
	{
		Client client = null;
		Shell parent = HandlerUtil.getActiveShell(a_event);
		try 
		{
			EmailWorker emailWorker = new EmailWorker();
			if (!emailWorker.checkEmailFile())
			{
				EmailWindow emailWindow = new EmailWindow();
				emailWindow.show();
			}
			
			ReportArchiveCreator archiveCreator = new ReportArchiveCreator();
			archiveCreator.createReportArchive();
			client = new Client();
			client.sendReportArchive(archiveCreator.getReportArchivePath());
			MessageDialog.openInformation(parent, m_messageTitle, m_successMessage);
		} 
		catch (IOException | InterruptedException e) 
		{
			MessageDialog.openError(parent, m_messageTitle, m_errorMessage + e.getMessage());
		}
		finally 
		{
			if (client != null) client.deleteArchive();
		}
		return null;
	}
}
