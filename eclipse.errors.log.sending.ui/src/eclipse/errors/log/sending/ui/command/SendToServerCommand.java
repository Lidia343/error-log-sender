package eclipse.errors.log.sending.ui.command;

import java.io.FileNotFoundException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import eclipse.errors.log.sending.core.ReportArchiveCreator;
import eclipse.errors.log.sending.core.client.Client;
import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.ui.email.EmailWindow;

/**
 * Команда для создания архива и отправки его на
 * сервер.
 */
public class SendToServerCommand extends AbstractHandler
{
	/**
	 * Если email пользователя не найден в настройках,
	 * вызывает метод для создания окна для его ввода.
	 * Если email присутствует в настройках (изначально
	 * или после нажатия на кнопку "OK" диалогового окна
	 * для ввода адреса), создаёт и отправляет архив на
	 * сервер.
	 * Удаляет созданный архив вне зависимости от того,
	 * было ли установлено соединение с сервером, и от
	 * ответа сервера.
	 */
	@Override
	public Object execute (ExecutionEvent a_event) throws ExecutionException
	{
		String messageTitle = "Отправка отчёта об ошибке";
		Client client = null;
		try
		{
			client = new Client();
			if (needsExecute())
			{
				ReportArchiveCreator archiveCreator = new ReportArchiveCreator();
				archiveCreator.createReportArchive();
				client.sendReportArchive(archiveCreator.getReportArchivePath());
				MessageDialog.openInformation(HandlerUtil.getActiveShell(a_event), messageTitle,
											  "Отчёт об ошибке успешно отправлен");
			}
		}
		catch (Exception e) 
		{
			if (!(e instanceof FileNotFoundException))
			{
				MessageDialog.openError(HandlerUtil.getActiveShell(a_event), messageTitle,
										"Произошла ошибка. Подробная информация:" + System.lineSeparator() +
										e.getMessage());
			}
		}
		finally
		{
			if (client != null)
			{
				client.deleteArchive();
			}
		}
		return null;
	}
	
	/**
	 * @return если email пользователя найден в настройках,
	 * возвращает true. Иначе вызывает метод для создания окна
	 * для его ввода. Если в данном окне была нажата кнопка
	 * "OK" и был введён корректный email, возвращает true,
	 * иначе - false.
	 */
	private boolean needsExecute () throws Exception
	{
		if (!new EmailChecker().checkEmailPreference()) 
		{
			if (new EmailWindow().show() == IDialogConstants.OK_ID)
			{
				return true;
			}
			return false;
		}
		return true;
	}
}
