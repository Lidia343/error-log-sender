package eclipse.errors.log.sending.ui.email;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.client.Client;
import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.core.util.AppUtil;

public class EmailPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	private static class ValueChangeController
	{
		private static ValueChangeController instance;
		
		private String m_sentEmail = "";
		
		private ValueChangeController ()
		{
		}
		
		public static ValueChangeController getInstance ()
		{
			if (instance == null) instance = new ValueChangeController();
			return instance;
		}
		
		public boolean isChangeForSending (String a_oldValue, String a_newValue) throws IOException
		{
			if (m_sentEmail.equals(a_newValue)) return false;
			
			m_sentEmail = a_newValue;
			
			return true;
		}
	}
	
	private StringFieldEditor m_stringFieldEditor;
	
	private final ValueChangeController m_valueChangeController;
	
	private String m_oldValue = "";
	
	public EmailPreferencePage() 
	{
		super(GRID);
		m_valueChangeController = ValueChangeController.getInstance();
	}
	
	@Override
	public void init (IWorkbench a_workbench) 
	{
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, AppUtil.PLUGIN_ID);
		
		scopedPreferenceStore.addPropertyChangeListener(new IPropertyChangeListener () 
		{
			@Override
			public void propertyChange (PropertyChangeEvent a_event) 
			{
				if (a_event.getProperty().equals(EmailChecker.EMAIL_KEY))
				{
					String oldValue = a_event.getOldValue().toString();
					String newValue = a_event.getNewValue().toString();
					
					EmailChecker emailChecker = new EmailChecker();
					if (!emailChecker.checkEmail(newValue))
					{
						try
						{
							m_oldValue = oldValue;
							AppUtil.putEmailPreference(oldValue);
							m_stringFieldEditor.setStringValue(oldValue);
						}
						catch (BackingStoreException e)
						{
							e.printStackTrace();
						}
					}
					else if (!newValue.equals(m_oldValue) && (emailChecker.checkEmail(oldValue) || oldValue.equals("")))
					{
						try 
						{
							if (m_valueChangeController.isChangeForSending(oldValue, newValue))
							{
								new Client().sendEmail(oldValue, newValue);
							}
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
				}	
			}
		});
		
		setPreferenceStore(scopedPreferenceStore);
        setDescription("Изменение адреса электронной почты для обратной связи.");
	}

	@Override
	protected void createFieldEditors() 
	{
		m_stringFieldEditor = new StringFieldEditor(EmailChecker.EMAIL_KEY, "Email:", getFieldEditorParent());
		m_stringFieldEditor.setTextLimit(EmailChecker.EMAIL_TEXT_LIMIT);
        addField(m_stringFieldEditor);
	}
}
