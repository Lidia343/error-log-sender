package eclipse.errors.log.sending.ui.email;

import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.core.email.IEmailSavingListener;
import eclipse.errors.log.sending.core.util.AppUtil;

public class EmailWindow
{
	private static IEmailSavingListener m_emailSavinglistener;
	
	public EmailWindow (IEmailSavingListener a_emailSavinglistener)
	{
		m_emailSavinglistener = a_emailSavinglistener;
	}
	
	public void show () throws IOException, InterruptedException, BackingStoreException
	{
		String message = "Перед отправкой отчёта на сервер необходимо указать адрес электронной почты для обратной связи." +
				         "В дальнейшем возможно изменение адреса в настройках.";

		InputDialog emailDialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Ввод адреса электронной почты", 
				                                  message, "name@example.com", new IInputValidator ()
		{
			@Override
			public String isValid(String a_newText) 
			{
				if (a_newText.equals("")) return "";
				
				if (!new EmailChecker().checkEmail(a_newText))
				{
					return "Некорректный адрес электронной почты.";
				}
				return null;
			}
	
		});
		InputDialog.setDefaultImage(getShellImage());
		emailDialog.create();
		emailDialog.open();
		
		if (emailDialog.getReturnCode() == IDialogConstants.OK_ID)
		{
			AppUtil.putEmailPreference(emailDialog.getValue());
			m_emailSavinglistener.emailSaved();
		}
	}
	
	private static Image getShellImage ()
	{
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		return sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage();
	}
}
