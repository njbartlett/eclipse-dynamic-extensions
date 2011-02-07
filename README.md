Dynamic Extensions
==================

Programmatic Contribution
-------------------------

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

Declarative Services Integration
--------------------------------

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

### Disposal ###

A limitation of the Eclipse extension APIs is that there is no consistent way for an extension object to be notified that it is no longer needed.

For example, when the workbench user closes a view, the ViewPart's `dispose()` method will be called in order to dispose SWT controls, images, fonts and colours. However the `dispose()` method is defined on the `IWorkbenchPart` interface and is not universal across all extension objects... e.g. an `IApplication` has a `stop()` method.

If an extension object has been supplied from a `ComponentFactory` then the `dispose()` method of `ComponentInstance` must be called when the extension is no longer used. Because of the lack of a consistent API, we must ask the extension object itself to notify us when it can be disposed.

For this purpose two interfaces are defined, `IDisposable` and `IDisposer`. The first should be implemented by our extension objects; the framework will call the `setDisposer()` method shortly after construction to pass an `IDisposer`. When the object needs to clean up, it calls `disposer.disposer()`.

Now we can see the full code for the view:

	public class TestView extends ViewPart implements IDisposable {
	
		private IDisposer disposer;
		private PackageAdmin pkgAdmin;

		// Test some DS features
		protected void activate() {
			System.out.println("TestView activated by SCR");
		}
		protected void deactivate() {
			System.out.println("TestView deactivated by SCR");
		}
		protected void setPackageAdmin(PackageAdmin pkgAdmin) {
			System.out.println("TestView.setPackageAdmin() invoked");
			this.pkgAdmin = pkgAdmin;
		}
		protected void unsetPackageAdmin(PackageAdmin pkgAdmin) {
		}

		// Standard RCP view methods
		@Override
		public void createPartControl(Composite parent) {
			new Label(parent, SWT.NONE).setText("Hello World");
			parent.setLayout(new FillLayout());
		}
		@Override
		public void setFocus() {
		}

		// Take care of disposal
		public void setDisposer(IDisposer disposer) {
			this.disposer = disposer;
		}
		@Override
		public void dispose() {
			System.out.println("View disposed by workbench, calling disposer...");
			disposer.dispose();
			super.dispose();
		}

	}

Registry Token
--------------

All programmatic modifications of the extension registry are protectd by a registry "token". This is an object that is defined by the initialiser of the extension registry, and it is not generally visible to other plugins.

In an ordinary Eclipse RCP or SDK application, the registry is initialised by the `org.eclipse.core.runtime` plugin using a non-null token that is not accessible by any other plug-in. Therefore programmatic registry manipulation is ordinarily not possible.

Therefore, two ways exist to obtain access to the registry:

* turn off the extension registry implementation supplied by `org.eclipse.core.runtime`, and create our own registry with a token of our choice, or;
* pass a system property instructing the core runtime to use a null token.

The second of these is by far the simplest. Just ensure that the following switch is supplied when launching the application:

	-Declipse.registry.nulltoken=true

Note that both of these approaches require us to have control over the launching of the application. This implies that programmatic registry manipulation is not possible for plug-ins installed into an existing application such as the Eclipse IDE.