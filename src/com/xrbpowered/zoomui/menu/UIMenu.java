package com.xrbpowered.zoomui.menu;

import java.awt.Color;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.std.UIListBox;

public class UIMenu extends UIContainer {

	public static Color colorBackground = new Color(0xf2f2f2);
	public static Color colorBorder = UIListBox.colorBorder;

	public UIMenu(UIContainer parent) {
		super(parent);
	}
	
	@Override
	public void layout() {
		float max = 0f;
		for(UIElement c : children) {
			UIMenuItem mi = (UIMenuItem) c;
			float w = mi.getMinWidth();
			if(w>max)
				max = w;
		}
		float y = 0f;
		for(UIElement c : children) {
			c.setLocation(0, y);
			float h = c.getHeight();
			c.setSize(max, h);
			y += h;
		}
		setSize(max, y);
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, colorBackground);
	}
	
	@Override
	protected void paintChildren(GraphAssist g) {
		super.paintChildren(g);
		g.resetStroke();
		g.border(this, colorBorder);
	}

	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		return true;
	}
	
}
