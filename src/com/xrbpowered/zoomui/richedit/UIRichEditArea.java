package com.xrbpowered.zoomui.richedit;

import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIScrollContainer;

public class UIRichEditArea extends UIScrollContainer {

	public final UIRichEdit editor;
	
	public UIRichEditArea(UIContainer parent) {
		super(parent);
		editor = createEditor();
	}
	
	protected UIRichEdit createEditor() {
		return new UIRichEdit(getView(), false);
	}
	
	@Override
	protected float layoutView() {
		editor.setLocation(0, 0);
		editor.updateSize();
		return editor.getHeight();
	}
	
}
