package name.njbartlett.eclipse.dynext.scr;

import name.njbartlett.eclipse.dynext.contribute.IDisposer;

import org.osgi.service.component.ComponentInstance;

class ComponentInstanceDisposer implements IDisposer {
	
	private final ComponentInstance instance;

	public ComponentInstanceDisposer(ComponentInstance instance) {
		this.instance = instance;
	}

	public void dispose() {
		instance.dispose();
	}

}
