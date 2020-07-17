package eclipse.errors.log.sending.ui.email;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.core.util.AppUtil;

public class EmailPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	private StringFieldEditor m_stringFieldEditor;
	
	public EmailPreferencePage() 
	{
		super(GRID);
	}

	@Override
	public void init(IWorkbench a_workbench) 
	{
		IScopeContext context = InstanceScope.INSTANCE;
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(context, AppUtil.PLUGIN_ID);
		
		scopedPreferenceStore.addPropertyChangeListener(new IPropertyChangeListener () 
		{
			@Override
			public void propertyChange(PropertyChangeEvent a_event) 
			{
				if (a_event.getProperty().equals(EmailChecker.EMAIL_KEY))
				{
					EmailChecker emailChecker = new EmailChecker();
					
					String oldValue = a_event.getOldValue().toString();

					if (!emailChecker.checkEmail(a_event.getNewValue().toString()))
					{
						try
						{
							AppUtil.putEmailPreference(oldValue);
							m_stringFieldEditor.setStringValue(oldValue);
						} 
						catch (BackingStoreException e)
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
