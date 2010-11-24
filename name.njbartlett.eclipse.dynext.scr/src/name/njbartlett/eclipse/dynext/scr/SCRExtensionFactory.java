package name.njbartlett.eclipse.dynext.scr;

import name.njbartlett.eclipse.dynext.contribute.IDisposable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

public class SCRExtensionFactory implements IExecutableExtension,
		IExecutableExtensionFactory {

	private final ComponentFactory componentFactory;
	
	private IConfigurationElement config;
	private String propertyName;
	
	public SCRExtensionFactory(ComponentFactory componentFactory) {
		this.componentFactory = componentFactory;
	}

	public Object create() throws CoreException {
		ComponentInstance instance = componentFactory.newInstance(null);
		ComponentInstanceDisposer disposer = new ComponentInstanceDisposer(instance);
		
		Object result = instance.getInstance();
		if (result == null)
			throw new CoreException(new Status(IStatus.ERROR, Constants.BUNDLE_ID, 0, "Component configuration has been deactivated.", null));
		
		try {
			if (result instanceof IExecutableExtension)
				((IExecutableExtension) result).setInitializationData(config, propertyName, null);
		} catch (CoreException e) {
			instance.dispose();
			throw new CoreException(new Status(IStatus.ERROR, Constants.BUNDLE_ID, 0, "Error occurred while setting component instance initialisation data.", e));
		}
		
		if (result instanceof IDisposable)
			((IDisposable) result).setDisposer(disposer);
		
		return result;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		this.config = config;
		this.propertyName = propertyName;
	}

}
