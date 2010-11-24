package name.njbartlett.eclipse.dynext.scr;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		new ExtensionRegistryTracker(context).open();
	}

	public void stop(BundleContext context) throws Exception {
	}

}
