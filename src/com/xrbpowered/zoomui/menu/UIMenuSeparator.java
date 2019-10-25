package com.xrbpowered.zoomui.menu;

import java.awt.Color;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class UIMenuSeparator extends UIElement {

	public static Color color = new Color(0xdddddd);
	public static float margin = 2;
	
	public UIMenuSeparator(UIContainer parent) {
		super(parent);
		setSize(0, margin*2+1);
	}

	@Override
	public void paint(GraphAssist g) {
		g.line(0, margin+1, getWidth(), margin+1, color);
	}

}
