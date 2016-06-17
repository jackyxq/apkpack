package com.jacky.app.pack.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

@SuppressWarnings("serial")
public class CloseLabel extends JPanel {

	public interface OnCloseListener {
		public void onClose(CloseLabel e);
	}
	
	private JLabel label, closeBtn;
	private OnCloseListener mListener;
	
	public CloseLabel(String text) {
		setLayout(new OverlayLayout(this));
		init(text);
	}
	
	private void init(String text) {
		label = new JLabel(text);
		label.setFont(new Font("标楷体",Font.PLAIN,18));	
		add(label);
		
		closeBtn = new JLabel("X");
		closeBtn.setFont(new Font("标楷体",Font.PLAIN,21));
		closeBtn.setForeground(Color.red);
		closeBtn.setVisible(false);
		closeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(mListener != null) {
					mListener.onClose(CloseLabel.this);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				closeBtn.setVisible(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				closeBtn.setVisible(false);
			}
		});
		add(closeBtn);
		
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
			));
		
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				closeBtn.setVisible(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				closeBtn.setVisible(false);
			}
		});
	}
	
//	@Override
//	public void setFont(Font font) {
//		if(label == null) return;
//		label.setFont(font);
//    }
	
	public void setOnCloseListener(OnCloseListener l) {
		this.mListener = l;
	}
	
	@Override
	public void addMouseListener(MouseListener l) {
		//不可用
	}
	
	public String getText() {
		return label.getText();
	}
}
