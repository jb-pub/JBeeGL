package com.jbee.rcp.gl.app;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.jbee.rcp.gl.view.GLPart;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		layout.addStandaloneView(GLPart.ID,  false, IPageLayout.LEFT, 1.0f, editorArea);
	}

}
