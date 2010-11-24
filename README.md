Dynamic Extensions
==================

1. Programmatic Contribution
----------------------------

The `name.njbartlett.eclipse.dynext` can be used to programmatically make dynamic contributions to the Eclipse extension registry. First a simple example:

Extensions are contributed via an XML file in the same format as `plugin.xml`:

	<plugin>
	   <extension point="name.njbartlett.eclipse.dynext.bbtests.sampleExtPoint">
	      <sample
	            class="name.njbartlett.eclipse.dynext.bbtests.SampleImplementation"
	            id="sample1"
	            name="Sample Name">
	      </sample>
	   </extension>
	</plugin>

Note that no `id` attribute needs to be declared on the `extension` element itself. In fact an ID will be added automatically.

To contribute this extension:

	InputStream extensionInput = // .. get an InputStream for the XML
	Bundle myBundle = // ... get reference to our bundle
	Contribution contribution = Contribution.loadContribution(
			extensionInput, myBundle);

	// Install the extension
	IExtensionRegistry registry = Platform.getExtensionRegistry();
	boolean installed = contribution.install(registry, "name", null);

	// Uninstall
	contribution.uninstall(registry, null);

Now for a more useful example. We often want to control the instance of the extension class, so that we add runtime context, for example injecting service dependencies. To do this, change the XML as follows:

	<plugin>
	   <extension
	         point="name.njbartlett.eclipse.dynext.bbtests.sampleExtPoint">
	      <sample
	            class="@FACTORY_ID=foo"
	            id="name.njbartlett.eclipse.dynext.bbtests.sample1"
	            name="Sample Name">
	      </sample>
	   </extension>
	</plugin>

Now write the programmatic contribution code as follows:

	Contribution contribution = Contribution.loadContribution(
			extensionInput, myBundle);

	// Set a factory to return a specific object.
	final ISampleInterface myInstance = new SampleImplementation();
	contribution.setFactoryObject("foo", new IExecutableExtensionFactory() {
		public Object create() throws CoreException {
			return myInstance;
		}
	});

	// Install the extension
	IExtensionRegistry registry = Platform.getExtensionRegistry();

We now have full programmatic control over the instances created from the extension.

2. Declarative Services Integration
-----------------------------------

The programmatic layer can be used as the base for integrating component models such as Declarative Services (DS). The DS integration exists in the `name.njbartlett.eclispe.dynext.scr` plug-in, which works as an extender bundle.

In this example we will create an Eclipse View (IViewPart) that is injected with service dependencies (in this case, the PackageAdmin service). First the DS component.xml:

	<scr:component xmlns:scr="http://www.osgi.org/xmlns/scr/v1.1.0"
			factory="@dynamicExt:sample-plugin.xml/testView"
			name="org.example.scr">
		<implementation class="org.example.scr.TestView"/>
		<reference interface="org.osgi.service.packageadmin.PackageAdmin"
				bind="setPackageAdmin"
				unbind="unsetPackageAdmin"/>
	</scr:component>

Note that this creates a Component Factory rather than a singleton comonent. For extensions we always need a factory, since the Eclipse workbench can create multiple instances over time. Also note the syntax of the factory ID attribute: this indicates that the extension XML is in `sample-plugin.xml` and the factory with ID "testView" should be wired into this Component Factory.

The content of `sample-plugin.xml` is as follows:

	<?xml version="1.0" encoding="UTF-8"?>
	<?eclipse version="3.4"?>
	<plugin>
	   <extension point="org.eclipse.ui.views">
	      <view
	            id="org.example.scr.testView"
	            name="Test"
	            class="@FACTORY_ID=testView">
	      </view>
	   </extension>
	</plugin>

This is identical to the normal content of `plugin.xml` for a View declaration, except for the class attribute.