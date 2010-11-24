package name.njbartlett.eclipse.dynext.scr;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class ExtensionRegistryTracker extends ServiceTracker {
	
	public ExtensionRegistryTracker(BundleContext context) {
		super(context, IExtensionRegistry.class.getName(), null);
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		IExtensionRegistry registry = (IExtensionRegistry) context.getService(reference);
		
		ComponentFactoryTracker tracker = new ComponentFactoryTracker(context, registry, "dynamic", null);
		tracker.open();
		
		return tracker;
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		ComponentFactoryTracker tracker = (ComponentFactoryTracker) service;
		tracker.close();
		
		context.ungetService(reference);
	}
}
