package com.jacky.app.pack.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.jacky.app.pack.PackFrame;

public class EscListener implements KeyListener, WindowListener {

	private PackFrame frame;
	
	public EscListener(JFrame f) {
		frame = (PackFrame) f;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
            save();
            System.exit(0);
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		save();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	private void save() {
		frame.saveData();
	}
}
