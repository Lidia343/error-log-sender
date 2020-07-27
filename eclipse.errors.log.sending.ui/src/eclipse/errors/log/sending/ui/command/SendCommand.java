package eclipse.errors.log.sending.ui.command;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.ui.email.EmailWindow;
import eclipse.errors.log.sending.core.ReportArchiveCreator;
import eclipse.errors.log.sending.core.client.Client;

/**
 * Команда для создания и отправки отчёта на сервер.
 */
public class SendCommand extends AbstractHandler
{
	private final String m_messageTitle = "Отправка отчёта об ошибке";
	
	private Shell m_parent = null;
	private Client m_client = null;
	
	/**
	 * Если email пользователя не найлен в настройках, вызывает метод для создания
	 * окна для его ввода. 
	 * Если email присутствует в настройках (изначально или после нажатия на кнопку 
	 * "OK" диалогового окна для ввода адреса), вызывает метод  "createAndSendReportArchive".
	 * Удаляет созданный архив вне зависимости от того, было ли установлено соединение
	 * с сервером, и от ответа сервера.
	 */
	@Override
	public Object execute (ExecutionEvent a_event) throws ExecutionException 
	{
		m_parent = HandlerUtil.getActiveShell(a_event);
		try 
		{
			if (!new EmailChecker().checkEmailPreference()) 
			{
				if (new EmailWindow().show() == IDialogConstants.OK_ID)
				{
					createAndSendReportArchive();
				}
			}
			else createAndSendReportArchive();
		} 
		catch (IOException | InterruptedException | BackingStoreException e) 
		{
			if (!(e instanceof FileNotFoundException))
			{
				MessageDialog.openError(m_parent, m_messageTitle, "Произошла ошибка. Подробная информация:" + System.lineSeparator() + e.getMessage());
			}
		}
		finally 
		{
			if (m_client != null) m_client.deleteArchive();
		}
		return null;
	}
	
	/**
	 * Вызывает методы для создания архива и отправки его на сервер.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void createAndSendReportArchive () throws IOException, InterruptedException
	{
		ReportArchiveCreator archiveCreator = new ReportArchiveCreator();
		archiveCreator.createReportArchive();
		m_client = new Client();
		m_client.sendReportArchive(archiveCreator.getReportArchivePath());
		MessageDialog.openInformation(m_parent, m_messageTitle, "Отчёт об ошибке успешно отправлен");
	}
}
