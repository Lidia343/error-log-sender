package eclipse.errors.log.sending.ui.command;

import java.io.FileNotFoundException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import eclipse.errors.log.sending.core.ReportArchiveCreator;
import eclipse.errors.log.sending.core.util.AppUtil;

/**
 * Команда для создания архива и сохранения его в файловой
 * системе.
 */
public class SaveToFileCommand extends AbstractHandler
{
	/**
	 * Создаёт архив и сохраняет его по пути, указанному
	 * пользователем в диалоговом окне.
	 * Если путь не был указан, ничего не делает.
	 */
	@Override
	public Object execute (ExecutionEvent a_event) throws ExecutionException 
	{
		String messageTitle = "Сохранение отчёта об ошибке";
		try
		{
		    String filePath = openFileDialog(HandlerUtil.getActiveShell(a_event), messageTitle);
			if (filePath != null)
			{
				new ReportArchiveCreator().createReportArchive(filePath);
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
		return null;
	}
	
	private String openFileDialog (Shell a_parent, String a_title)
	{
		FileDialog dialog = new FileDialog(a_parent, SWT.SAVE);
		dialog.setText(a_title);
		dialog.setFileName(ReportArchiveCreator.ARCHIVE_NAME + " (" + AppUtil.getCurrentDateAndTime() + ").zip");
		dialog.setFilterNames(new String[]{"Архив (*.zip)"});
	    dialog.setFilterExtensions(new String[]{"*.zip"});
	    return dialog.open();
	}
}
