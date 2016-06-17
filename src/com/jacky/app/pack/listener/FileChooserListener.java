package com.jacky.app.pack.listener;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.jacky.app.pack.ConfigManager;
import com.jacky.app.pack.PackFrame;

public class FileChooserListener implements ActionListener, MouseListener {

	private PackFrame parent;
	
	public FileChooserListener(Frame p) {
		parent = (PackFrame) p;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		showDialog();
	}

	private void showDialog() {
		ConfigManager m = parent.getConfigManager();
		
		JFileChooser chooser = new JFileChooser(m.getLastApkPath());
		chooser.setDialogTitle("请选择生成渠道包的APK");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "APK应用程序(*.apk)";
			}
			
			@Override
			public boolean accept(File f) {
				if(f.isDirectory()) return true;
				return f.getName().endsWith(".apk");
			}
		});
		int i = chooser.showOpenDialog(parent);
		if(JFileChooser.APPROVE_OPTION == i) {
			String path = chooser.getSelectedFile().getAbsolutePath();
			m.saveLastApkPath(path);
			parent.setApkPath(path);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		showDialog();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
