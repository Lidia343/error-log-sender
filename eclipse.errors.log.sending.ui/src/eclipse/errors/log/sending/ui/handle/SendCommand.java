package eclipse.errors.log.sending.ui.handle;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eclipse.errors.log.sending.core.ReportArchiveCreator;

public class SendCommand extends AbstractHandler
{
	private final String m_messageTitle = "Результат попытки отправки отчёта";
	private final String m_successMessage = "Отчёт об ошибке успешно отправлен";
	private final String m_errorMessage = "Произошла ошибка. Подробная информация:\n";
	
	@Override
	public Object execute(ExecutionEvent a_event) throws ExecutionException 
	{
		try 
		{
			ReportArchiveCreator archiveCreator = new ReportArchiveCreator();
			archiveCreator.createReportArchive();
			MessageDialog.openInformation(HandlerUtil.getActiveShell(a_event), m_messageTitle, m_successMessage);
		} 
		catch (IOException e) 
		{
			MessageDialog.openError(HandlerUtil.getActiveShell(a_event), m_messageTitle, m_errorMessage + e.getMessage());
		}
		return null;
	}
}
