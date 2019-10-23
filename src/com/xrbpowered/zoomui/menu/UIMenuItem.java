package com.xrbpowered.zoomui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIButtonBase;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class UIMenuItem extends UIButtonBase {

	public static Font font = UIButton.font;
	
	public static Color colorText = Color.BLACK;
	public static Color colorHover = UITextBox.colorSelection;
	public static Color colorHoverText = UITextBox.colorSelectedText;

	public static int defaultHeight = 24;
	public static int marginLeft = 16;
	public static int marginRight = 32;
	
	public String label;
	
	public UIMenuItem(UIContainer parent, String label) {
		super(parent);
		this.label = label;
		setSize(0, defaultHeight);
	}
	
	public float getMinWidth() {
		FontMetrics fm = getBase().getWindow().getFontMetrics(font, font.getSize(), getPixelScale());
		return fm.stringWidth(label)+getTotalMargins();
	}

	public float getMarginLeft() {
		return marginLeft;
	}

	public float getTotalMargins() {
		return marginLeft+marginRight;
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
		g.drawString(label, getMarginLeft(), getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
	}
}
