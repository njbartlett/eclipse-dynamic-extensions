package name.njbartlett.eclipse.dynext.scr;

import java.io.IOException;
import java.net.URL;

import name.njbartlett.eclipse.dynext.contribute.Contribution;
import name.njbartlett.eclipse.dynext.contribute.ContributionProcessingException;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.util.tracker.ServiceTracker;

public class ComponentFactoryTracker extends ServiceTracker {
	
	private static final String PREFIX_DYNAMIC_EXT = "@dynamicExt:";
	private final IExtensionRegistry registry;
	private final String name;
	private final Object token;

	public ComponentFactoryTracker(BundleContext context, IExtensionRegistry registry, String name, Object token) {
		super(context, ComponentFactory.class.getName(), null);
		
		this.registry = registry;
		this.name = name;
		this.token = token;
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		ExtensionFactoryAddress factoryRef = extractExtensionFactoryAddress(reference);
		if (factoryRef != null) {
			Bundle bundle = reference.getBundle();
			URL contribXmlUrl = bundle.getEntry(factoryRef.getContributionXmlPath());
			if (contribXmlUrl != null) {
				try {
					Contribution contribution = Contribution.loadContribution(contribXmlUrl.openStream(), bundle);
					ComponentFactory service = (ComponentFactory) context.getService(reference);
					if (service != null) {
						SCRExtensionFactory extensionFactory = new SCRExtensionFactory(service);
						contribution.setFactoryObject(factoryRef.getFactoryId(), extensionFactory);
						
						if (contribution.install(registry, name, token)) {
							return contribution;
						}
					}
				} catch (ContributionProcessingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private ExtensionFactoryAddress extractExtensionFactoryAddress(ServiceReference svcRef) {
		String text = (String) svcRef.getProperty(ComponentConstants.COMPONENT_FACTORY);
		if (text.startsWith(PREFIX_DYNAMIC_EXT)) {
			String path = text.substring(PREFIX_DYNAMIC_EXT.length());
			int slashIndex = path.indexOf('/');
			if (slashIndex > -1 && slashIndex + 1 < path.length()) {
				return new ExtensionFactoryAddress(path.substring(0, slashIndex),
						path.substring(slashIndex + 1));
			}
		}
		return null;
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		// Uninstall the contribution
		Contribution contribution = (Contribution) service;
		contribution.uninstall(registry, token);
		
		// Stop tracking the component factory service
		context.ungetService(reference);
	}
}
