package org.example.scr;

import name.njbartlett.eclipse.dynext.contribute.IDisposable;
import name.njbartlett.eclipse.dynext.contribute.IDisposer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.packageadmin.PackageAdmin;

public class TestView extends ViewPart implements IDisposable {
	
	private IDisposer disposer;
	private PackageAdmin pkgAdmin;

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

	@Override
	public void createPartControl(Composite parent) {
		new Label(parent, SWT.NONE).setText("Hello World");
		parent.setLayout(new FillLayout());
	}

	@Override
	public void setFocus() {
	}

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
