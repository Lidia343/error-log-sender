package eclipse.errors.log.sending.ui.email;

import java.io.File;
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
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.core.email.IEmailSavingListener;
import eclipse.errors.log.sending.core.util.AppUtil;
import eclipse.errors.log.sending.ui.command.SendCommand;

public class EmailWindow
{
	private final String m_openedEmailWindow = "opened";
	private final String m_closedEmailWindow = "closed";
	
	private final String m_statusFilePath = System.getProperty("java.io.tmpdir") + File.separator + "emailWindowStatus.txt";
	
	private Shell m_parent;
	private IEmailSavingListener m_emailSavinglistener;
	
	public EmailWindow (IEmailSavingListener a_emailSavinglistener)
	{
		m_emailSavinglistener = a_emailSavinglistener;
	}
	
	public void show () throws IOException
	{
		String status = AppUtil.readFromFile(m_statusFilePath);
		if (status != null && status.equals(m_openedEmailWindow)) return;
		
		AppUtil.writeToFile(m_statusFilePath, m_openedEmailWindow);
		
		m_parent = new Shell(Display.getCurrent());
		m_parent.setText("Ввод адреса электронной почты");
		m_parent.setMinimumSize(380, 150);
		m_parent.setImage(getShellImage());
		
		GridLayout layout = new GridLayout(1, true);
		m_parent.setLayout(layout);
		
		Composite composite = new Composite(m_parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		GridData g = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(g);
		
		createLabel(composite, "Перед отправкой отчёта на сервер необходимо указать адрес электронной почты для обратной связи.", 2);
		createLabel(composite, "В дальнейшем возможно изменение адреса в настройках.", 2);
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		createLabel(composite, "Email: ", 1);
		
		Text emailText = new Text(composite , SWT.NONE);
		emailText.setLayoutData(g);
		emailText.setTextLimit(EmailChecker.EMAIL_TEXT_LIMIT);
		
		Button savingButton = new Button(composite, SWT.PUSH);
		savingButton.setText("Сохранить");
		savingButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		m_parent.setDefaultButton(savingButton);
		
		savingButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent a_e) 
			{
				String line = emailText.getText();
				try 
				{
					if (AppUtil.putEmailPreference(line))
					{
						MessageDialog.openInformation(m_parent, "Сохранение адреса почты", "Адрес электронной почты сохранён.");
						m_parent.dispose();
					}
					else
					{
						MessageDialog.openWarning(m_parent, "Проверка адреса почты", "Некорректный адрес электронной почты.");
					}
				} 
				catch (BackingStoreException e) 
				{
					e.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent a_e) 
			{
			}
		});
		
		m_parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent a_e)
			{
				try
				{
					AppUtil.writeToFile(m_statusFilePath, m_closedEmailWindow);
					m_parent.dispose();
					m_emailSavinglistener.emailSaved();
				}
				catch (Exception e) 
				{
					MessageDialog.openError(m_parent, "Ошибка", SendCommand.ERROR_MESSAGE + e.getMessage());
				}
			}
		});
		
		composite.pack();
		m_parent.pack();
		m_parent.open();
	}
	
	private Image getShellImage ()
	{
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		return sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage();
	}
	
	private void createLabel (Composite a_composite, String a_text, int a_horSpan)
	{
		Label label = new Label(a_composite , SWT.NONE);
		label.setText(a_text);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, a_horSpan, 1));
	}
}
