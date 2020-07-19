package eclipse.errors.log.sending.ui.email;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.core.email.IEmailSavingListener;
import eclipse.errors.log.sending.core.util.AppUtil;

public class EmailWindow
{
	public static final String EMAIL_LABEL_TEXT = "Email:";
	public static final String ERROR_MESSAGE = "Произошла ошибка. Подробная информация:" + System.lineSeparator();
	
	private static EmailWindow m_instance;
	
	private static IEmailSavingListener m_emailSavinglistener;
	
	private static boolean m_exitBySavingButton = false;
	
	private EmailWindow (IEmailSavingListener a_emailSavinglistener)
	{
		m_emailSavinglistener = a_emailSavinglistener;
	}
	
	public static void show (IEmailSavingListener a_emailSavinglistener) throws IOException
	{
		if (m_instance != null) return;
		
		m_instance = new EmailWindow(a_emailSavinglistener);
		
		Shell parent = new Shell(Display.getCurrent(), SWT.DIALOG_TRIM);
		parent.setText("Ввод адреса электронной почты");
		parent.setImage(getShellImage());
		
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		GridData g = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(g);
		
		createLabel(composite, "Перед отправкой отчёта на сервер необходимо указать адрес электронной почты для обратной связи.", 2);
		createLabel(composite, "В дальнейшем возможно изменение адреса в настройках.", 2);
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		createLabel(composite, EMAIL_LABEL_TEXT + " ", 1);
		
		Text emailText = new Text(composite , SWT.NONE);
		emailText.setLayoutData(g);
		emailText.setTextLimit(EmailChecker.EMAIL_TEXT_LIMIT);
		
		Button savingButton = new Button(composite, SWT.PUSH);
		savingButton.setText("Сохранить");
		savingButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		parent.setDefaultButton(savingButton);
		
		savingButton.addSelectionListener(new SelectionListener ()
		{
			@Override
			public void widgetSelected (SelectionEvent a_selEv) 
			{
				try 
				{
					if (AppUtil.putEmailPreference(emailText.getText()))
					{
						MessageDialog.openInformation(parent, "Сохранение адреса почты", "Адрес электронной почты сохранён.");
						m_exitBySavingButton = true;
						parent.dispose();
					}
					else
					{
						MessageDialog.openWarning(parent, "Проверка адреса почты", "Некорректный адрес электронной почты.");
					}
				} 
				catch (Exception a_e) 
				{
					MessageDialog.openError(parent, "Ошибка", ERROR_MESSAGE + a_e.getMessage());
				}
			}

			@Override
			public void widgetDefaultSelected (SelectionEvent a_e) 
			{
			}
		});
		
		parent.addDisposeListener(new DisposeListener ()
		{
			@Override
			public void widgetDisposed (DisposeEvent a_disEv)
			{
				try
				{
					m_instance = null;
					parent.dispose();
					if (m_exitBySavingButton) m_emailSavinglistener.emailSaved();
				}
				catch (Exception a_e) 
				{
					MessageDialog.openError(parent, "Ошибка", ERROR_MESSAGE + a_e.getMessage());
				}
			}
		});
		
		composite.pack();
		parent.pack();
		parent.open();
	}
	
	private static Image getShellImage ()
	{
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		return sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage();
	}
	
	private static void createLabel (Composite a_composite, String a_text, int a_horSpan)
	{
		Label label = new Label(a_composite , SWT.NONE);
		label.setText(a_text);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, a_horSpan, 1));
	}
}
