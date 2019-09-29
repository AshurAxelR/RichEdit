package com.xrbpowered.zoomui.richedit;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIWindow;
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
			protected void paintBorder(GraphAssist g) {
				g.hborder(this, GraphAssist.TOP, colorBorder);
			}
		};
		text.editor.setFont(new Font("Verdana", Font.PLAIN, 10), 10f);
		text.editor.setTokeniser(new LineTokeniser(new JavaContext()));
		text.editor.setText(loadString(TEST_INPUT));
		
		frame.show();
	}

}
