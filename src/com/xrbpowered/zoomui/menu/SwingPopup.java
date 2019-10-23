package com.xrbpowered.zoomui.menu;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import com.xrbpowered.zoomui.UIWindow;
import com.xrbpowered.zoomui.swing.BasePanel;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class SwingPopup extends UIWindow {

	public final JPopupMenu popup;
	public final BasePanel panel;

	public SwingPopup(SwingWindowFactory factory) {
		super(factory);
		
		popup = new JPopupMenu();
		popup.setBorder(BorderFactory.createEmptyBorder());
		popup.setLayout(new BorderLayout());
		
		panel = new BasePanel(this);
		popup.add(panel, BorderLayout.CENTER);
	}
	
	public boolean setClientSizeFor(Measurable m) {
		int w = (int)m.measureWidth();
		int h = (int)m.measureHeight();
		setClientSize(w, h);
		return w>0 && h>0;
	}

	@Override
	public int getClientWidth() {
		return panel.getWidth();
	}

	@Override
	public int getClientHeight() {
		return panel.getHeight();
	}

	@Override
	public void setClientSize(int width, int height) {
		panel.resize(width, height);
	}
	
	@Override
	public int getX() {
		return popup.getX();
	}
	
	@Override
	public int getY() {
		return popup.getY();
	}
	
	@Override
	public void moveTo(int x, int y) {
		popup.setLocation(x, y);
	}

	@Override
	public void center() {
	}

	@Override
	public void show() {
		throw new UnsupportedOperationException();
	}
	
	public void show(BasePanel invoker, float x, float y) {
		popup.show(invoker, (int)x, (int)y);
	}

	@Override
	public void repaint() {
		panel.repaint();
	}

	@Override
	public void setCursor(Cursor cursor) {
		panel.setCursor(cursor);
	}
	
	@Override
	public void close() {
	}

	@Override
	public int baseToScreenX(float x) {
		return panel.baseToScreenX(x);
	}

	@Override
	public int baseToScreenY(float y) {
		return panel.baseToScreenY(y);
	}

	@Override
	public float screenToBaseX(int x) {
		return panel.screenToBaseX(x);
	}

	@Override
	public float screenToBaseY(int y) {
		return panel.screenToBaseY(y);
	}

	@Override
	public FontMetrics getFontMetrics(Font font) {
		return panel.getFontMetrics(font);
	}
	
	public boolean isVisible() {
		return popup.isVisible();
	}

}
