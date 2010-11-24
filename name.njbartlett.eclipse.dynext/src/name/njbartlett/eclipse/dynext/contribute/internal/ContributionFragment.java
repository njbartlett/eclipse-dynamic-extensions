package name.njbartlett.eclipse.dynext.contribute.internal;

import org.eclipse.core.runtime.IExtensionRegistry;


public interface ContributionFragment {

	public abstract void uninstall(IExtensionRegistry registry, Object token);
	

}
