package name.njbartlett.eclipse.dynext.bbtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import name.njbartlett.eclipse.dynext.contribute.Contribution;
import name.njbartlett.eclipse.dynext.contribute.ContributionProcessingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ContributionTests {
	@Test
	public void addRemoveExtension() throws ContributionProcessingException, IOException, CoreException {
		InputStream extensionInput = getClass().getResourceAsStream("sample1.xml");
		Contribution contribution = Contribution.loadContribution(extensionInput, Activator.getContext().getBundle());
		
		// Install the extension
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		boolean installed = contribution.install(registry, "foo", null);
		assertTrue(installed);
		
		// Check we can find it via extension registry
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(Activator.PLUGIN_ID, "sampleExtPoint");
		assertNotNull(elements);
		assertEquals(1, elements.length);
		
		// Instantiate an object from the extension
		Object object = elements[0].createExecutableExtension("class");
		assertNotNull(object);
		assertTrue(object instanceof ISampleInterface);
		assertEquals("Hello Jim", ((ISampleInterface) object).sayHello("Jim"));
		
		// Remove the extension
		contribution.uninstall(registry, null);
		
		// Check we can no longer find it via extension registry
		elements = registry.getConfigurationElementsFor(Activator.PLUGIN_ID, "sampleExtPoint");
		assertEquals(0, elements.length);
	}
	
	@Test
	public void addContributionWithFactory() throws ContributionProcessingException, IOException, CoreException  {
		InputStream extensionInput = getClass().getResourceAsStream("sample2.xml");
		Contribution contribution = Contribution.loadContribution(extensionInput, Activator.getContext().getBundle());
		
		// Set a factory to return a specific extension object.
		final ISampleInterface expected = new SampleImplementation();
		contribution.setFactoryObject("foo", new IExecutableExtensionFactory() {
			public Object create() throws CoreException {
				return expected;
			}
		});
		
		// Install the extension
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		assertTrue(contribution.install(registry, "foo", null));
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(Activator.PLUGIN_ID, "sampleExtPoint");
		assertEquals(1, elements.length);
		
		// Get an object from it and check it was what we wanted
		Object extension = elements[0].createExecutableExtension("class");
		assertTrue(expected == extension);
		
		// Clean up contribution
		contribution.uninstall(registry, null);
		assertEquals(0, registry.getConfigurationElementsFor(Activator.PLUGIN_ID, "sampleExtPoint").length);
	}
}
