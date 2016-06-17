/**
 * android渠道包的打包工具
 */
package com.jacky.app.pack;

import javax.swing.WindowConstants;

import com.jacky.app.pack.listener.EscListener;

public class Main {

	public static void main(String[] args) {
		final PackFrame frame = new PackFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); 
		frame.setResizable(false);
//		frame.addKeyListener(new EscListener(frame));
		frame.addWindowListener(new EscListener(frame));
	}

}
