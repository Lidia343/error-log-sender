
package eclipse.errors.log.sending.ui;

import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ContributionFactory extends ExtensionContributionFactory
{
	private final String m_extensionPointId = "org.eclipse.ui.commands";
	private final String m_commandId = "eclipse.errors.log.sending.ui.sendCommand";
	private String m_commandLabel = "";

	public ContributionFactory()
	{
		super();
		setCommandLabel();
	}

	private void setCommandLabel ()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(m_extensionPointId);
		for (IConfigurationElement ext : extensions)
		{
			String id = ext.getAttribute("id");
			if (id != null && id.equals(m_commandId))
			{
				for (IConfigurationElement child : ext.getChildren())
				{
					if (child.getAttribute("name").equals("label"))
					{
						m_commandLabel = child.getAttribute("values");
					}
				}
			}
		}
	}
	
	@Override
	public void createContributionItems (IServiceLocator a_serviceLocator, IContributionRoot a_additions)
	{
		CommandContributionItemParameter par = new CommandContributionItemParameter(a_serviceLocator, "", m_commandId, CommandContributionItem.STYLE_PUSH);
        par.label = m_commandLabel;
        //par.icon = Activator.getImageDescriptor("icons/name...");
        CommandContributionItem item = new CommandContributionItem(par);
        item.setVisible(true);
        a_additions.addContributionItem(item, null);
	}
}
