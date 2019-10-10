package com.xrbpowered.zoomui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIHoverElement;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class UIMenuItem extends UIHoverElement {

	public static Font font = UIButton.font;
	
	public static Color colorText = Color.BLACK;
	public static Color colorHover = UITextBox.colorSelection;
	public static Color colorHoverText = UITextBox.colorSelectedText;

	public static int defaultHeight = 24;
	public static int marginLeft = 16;
	public static int marginRight = 32;
	
	public String label;
	
	private float minWidth = marginLeft+marginRight;
	
	public UIMenuItem(UIContainer parent, String label) {
		super(parent);
		this.label = label;
		setSize(minWidth, defaultHeight);
	}
	
	public float getMinWidth() {
		return minWidth;
	}

	@Override
	public void paint(GraphAssist g) {
		if(hover) {
			g.fill(this, colorHover);
			g.setColor(colorHoverText);
		}
		else {
			g.setColor(colorText);
		}
		g.setFont(font);
		g.drawString(label, marginLeft, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
		
		FontMetrics fm = g.getFontMetrics();
		float w = fm.stringWidth(label)+marginLeft+marginRight;
		if(w!=minWidth) {
			minWidth = w;
			getParent().invalidateLayout();
			repaint();
		}
	}
}
