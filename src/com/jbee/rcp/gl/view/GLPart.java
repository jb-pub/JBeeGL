package com.jbee.rcp.gl.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class GLPart extends ViewPart {

	public static final String ID = "com.jbee.rcp.gl.view.GLPart";
	public GLViewer top = null;

	@Override
	public void createPartControl(Composite arg0) {
		top = new GLViewer(arg0, SWT.NONE);
	}

	@Override
	public void setFocus() {

	}

}
