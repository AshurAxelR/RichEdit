package com.xrbpowered.zoomui.menu;

import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class UIMenuBar extends UIContainer {

	public final UIMenu menu;
	public final UIContainer content;
	
	public UIMenuBar(UIContainer parent) {
		super(parent);
		this.content = new UIContainer(this) {
			@Override
			public void layout() {
				for(UIElement c : children) {
					c.setLocation(0, 0);
					c.setSize(getWidth(), getHeight());
					c.layout();
				}
			}
		};
		this.menu = new UIMenu(this);
		setSize(0, UIMenuItem.defaultHeight);
	}
	
	@Override
	public void layout() {
		float y = menu.getHeight();
		menu.setSize(getWidth(), y);
		menu.setLocation(0, 0);
		content.setSize(getWidth(), getHeight()-y);
		content.setLocation(0, y);
		super.layout();
	}

}
