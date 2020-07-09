package eclipse.errors.log.sending.ui.handle;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eclipse.errors.log.sending.core.ReportArchiveCreator;
import eclipse.errors.log.sending.core.client.Client;
import eclipse.errors.log.sending.core.exceptions.BlockedThreadException;

public class SendCommand extends AbstractHandler
{
	private final String m_messageTitle = "Результат попытки отправки отчёта";
	private final String m_successMessage = "Отчёт об ошибке успешно отправлен";
	private final String m_errorMessage = "Произошла ошибка. Подробная информация:\n";
	
	@Override
	public Object execute (ExecutionEvent a_event) throws ExecutionException 
	{
		try 
		{
			ReportArchiveCreator archiveCreator = new ReportArchiveCreator();
			archiveCreator.createReportArchive();
			new Client().sendReportArchive(archiveCreator.getReportArchiveName());
			MessageDialog.openInformation(HandlerUtil.getActiveShell(a_event), m_messageTitle, m_successMessage);
		} 
		catch (IOException | InterruptedException | BlockedThreadException e) 
		{
			MessageDialog.openError(HandlerUtil.getActiveShell(a_event), m_messageTitle, m_errorMessage + e.getMessage());
		}
		return null;
	}
}
