package com.jacky.app.pack.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.jacky.app.pack.PackFrame;

public class AddQudaoListener implements ActionListener {

	private PackFrame mFrame;
	
	public AddQudaoListener(PackFrame f) {
		mFrame = f;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    Object obj = JOptionPane.showInputDialog(null,"","请输入渠道名",JOptionPane.PLAIN_MESSAGE,null,null,"");
	    if(obj == null) return;
	    
	    mFrame.addQudao(obj.toString());
	}

}
