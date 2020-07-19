package eclipse.errors.log.sending.ui.email;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import eclipse.errors.log.sending.core.email.EmailChecker;
import eclipse.errors.log.sending.core.util.AppUtil;

public class EmailPreferenceInitializer extends AbstractPreferenceInitializer 
{
	@Override
	public void initializeDefaultPreferences() 
	{
		new ScopedPreferenceStore(InstanceScope.INSTANCE, AppUtil.PLUGIN_ID).setDefault(EmailChecker.EMAIL_KEY, "");
	}
}
