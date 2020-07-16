package eclipse.errors.log.sending.ui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import eclipse.errors.log.sending.core.EmailWorker;

public class EmailWindow 
{
	public void show () throws IOException
	{
		EmailWorker emailWorker = new EmailWorker();
		if (emailWorker.checkEmailFile()) return;
		
		Shell shell = new Shell(Display.getCurrent());
		shell.setText("Ввод адреса электронной почты");
		shell.setMinimumSize(325, 75);
		
		GridLayout layout = new GridLayout(1, true);
		shell.setLayout(layout);
		
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label emailLabel = new Label(composite , SWT.NONE);
		emailLabel.setText("Email: ");
		emailLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		Text emailText = new Text(composite , SWT.NONE);
		emailText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		composite.pack();
		shell.pack();
		shell.open();
		
		emailWorker.writeInEmailFile("?");
	}
}
