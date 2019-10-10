package com.xrbpowered.zoomui.richedit;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.UIWindow;
import com.xrbpowered.zoomui.menu.UIMenu;
import com.xrbpowered.zoomui.menu.UIMenuItem;
import com.xrbpowered.zoomui.richedit.java.JavaContext;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class RichEditTest {
	
	private static final String TEST_INPUT = "src/com/xrbpowered/zoomui/richedit/UIRichEditBase.java";

	public static byte[] loadBytes(InputStream s) throws IOException {
		DataInputStream in = new DataInputStream(s);
		byte[] bytes = new byte[in.available()];
		in.readFully(bytes);
		in.close();
		return bytes;
	}
	
	public static String loadString(String path) {
		try {
			return new String(loadBytes(new FileInputStream(path)));
		} catch(IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private static UIMenu menu;
	
	public static void showMenu(float x, float y) {
		x = menu.getParent().baseToLocalX(x);
		y = menu.getParent().baseToLocalY(y);
		menu.setLocation(x, y);
		menu.setVisible(true);
		menu.repaint();
	}
	
	public static void main(String[] args) {
		UIWindow frame = new SwingFrame(SwingWindowFactory.use(), "RichEditTest", 1600, 900, true, false) {
			@Override
			public boolean onClosing() {
				confirmClosing();
				return false;
			}
		};
		
		UIRichEditArea text = new UIRichEditArea(frame.getContainer()) {
			@Override
			protected UIRichEditBase createEditor() {
				return new UIRichEditBase(getView(), false) {
					@Override
					public boolean onMouseDown(float x, float y, Button button, int mods) {
						if(button==Button.right) {
							float bx = localToBaseX(x);
							float by = localToBaseY(y);
							showMenu(bx, by);
							return true;
						}
						else
							return super.onMouseDown(x, y, button, mods);
					}
				};
			}
			@Override
			protected void paintBorder(GraphAssist g) {
				g.hborder(this, GraphAssist.TOP, colorBorder);
			}
		};
		text.editor.setFont(new Font("Verdana", Font.PLAIN, 10), 10f);
		text.editor.setTokeniser(new LineTokeniser(new JavaContext()));
		text.editor.setText(loadString(TEST_INPUT));
		
		UIContainer overlay = new UIContainer(frame.getContainer()) {
			@Override
			public boolean onMouseDown(float x, float y, Button button, int mods) {
				if(menu.isVisible()) {
					menu.setVisible(false);
					repaint();
					return true;
				}
				else
					return false;
			}
			@Override
			public UIElement getElementAt(float x, float y) {
				UIElement e = super.getElementAt(x, y);
				return e==this ? null : e;
			}
		};
		menu = new UIMenu(overlay);
		new UIMenuItem(menu, "Undo Typing");
		new UIMenuItem(menu, "Redo Typing");
		new UIMenuItem(menu, "Save");
		menu.setVisible(false);
		
		frame.show();
	}

}
