package com.jacky.app.pack;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * 打包进度条
 * @author lixinquan
 *
 */
@SuppressWarnings("serial")
public class ProcessDialog extends JDialog {

	JProgressBar bar;
	JLabel title;
	
	public ProcessDialog(JFrame f) {
		super(f, true);
		setSize(350, 150);
		setLocationRelativeTo(null);
		setLayout(null);
		setUndecorated(true);
	
		title = new JLabel("APK 打包中... 0%");
		title.setBounds(10, 10, 300, 35);
		add(title);
		
		bar = new JProgressBar(0, 100);
		bar.setValue(0);
		bar.setBounds(10, 50, 300, 30);
		add(bar);
	}
	
	/**
	 * 
	 * @param value 0-100
	 */
	public void setValue(int value) {
		title.setText(String.format("APK 打包中... %d%%", value));
		bar.setValue(value);
	}
}
