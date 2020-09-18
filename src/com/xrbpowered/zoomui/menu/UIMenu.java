package com.xrbpowered.zoomui.menu;

import java.awt.Color;
import java.awt.Rectangle;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.Measurable;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.std.UIListBox;

public class UIMenu extends UIContainer implements Measurable {

	public static Color colorBackground = new Color(0xf2f2f2);
	public static Color colorBorder = UIListBox.colorBorder;

	public UIMenu(UIContainer parent) {
		super(parent);
	}
	
	@Override
	public void layout() {
		float max = 0f;
		for(UIElement c : children) {
			if(!(c instanceof UIMenuItem))
				continue;
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
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		return true;
	}
	
	@Override
	public boolean isVisible(Rectangle clip) {
		return isVisible();
	}
	
	public float measureWidth() {
		float max = 0f;
		for(UIElement c : children) {
			if(!(c instanceof UIMenuItem))
				continue;
			UIMenuItem mi = (UIMenuItem) c;
			float w = mi.getMinWidth();
			if(w>max)
				max = w;
		}
		return max;
	}
	
	@Override
	public float measureHeight() {
		float y = 0f;
		for(UIElement c : children) {
			y += c.getHeight();
		}
		return y;
	}
}
