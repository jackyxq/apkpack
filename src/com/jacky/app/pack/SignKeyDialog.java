package com.jacky.app.pack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class SignKeyDialog extends JDialog {

	PackFrame frame;
	JTextField pathText, pwdText, aliasText, apwdText;
	
	public SignKeyDialog(PackFrame f) {
		super(f, true);
		frame = f;
		setSize(360, 250);
		setLocationRelativeTo(null);
		setLayout(null);
		setTitle("签名文件信息");
	
		JLabel label = new JLabel("文    件：");
		label.setBounds(10, 10, 80, 25);
		add(label);
		
		pathText = new JTextField();
		pathText.addMouseListener(new FileChooserListener());
		pathText.setBounds(80, 10, 250, 25);
		pathText.setEditable(false);
		add(pathText);
		
		label = new JLabel("密    码：");
		label.setBounds(10, 45, 80, 25);
		add(label);
		
		pwdText = new JTextField();
		pwdText.setBounds(80, 45, 250, 25);
		add(pwdText);
		
		label = new JLabel("别    名：");
		label.setBounds(10, 80, 80, 25);
		add(label);
		
		aliasText = new JTextField();
		aliasText.setBounds(80, 80, 250, 25);
		add(aliasText);
		
		label = new JLabel("别名密码：");
		label.setBounds(10, 115, 80, 25);
		add(label);
		
		apwdText = new JTextField();
		apwdText.setBounds(80, 115, 250, 25);
		add(apwdText);
		
		JButton btn = new JButton("保存");
		btn.setBounds(135, 160, 80, 35);
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		add(btn);
		
		SignInfo sign = frame.getConfigManager().getSignInfo();
		if(sign != null) {
			pathText.setText(sign.path);
			pwdText.setText(sign.pwd);
			aliasText.setText(sign.alias);
			apwdText.setText(sign.apwd);
		}
	}
	
	private void save() {
		String p = pathText.getText();
		String pw = pwdText.getText();
		String ali = aliasText.getText();
		String apw = apwdText.getText();
		frame.getConfigManager().saveSignInfo(p, pw, ali, apw);
		setVisible(false);
	}
	
	private class FileChooserListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			ConfigManager m = frame.getConfigManager();
			
			JFileChooser chooser = new JFileChooser(m.getLastApkPath());
			chooser.setDialogTitle("请选择签名文件");
			int i = chooser.showOpenDialog(SignKeyDialog.this);
			if(JFileChooser.APPROVE_OPTION == i) {
				String path = chooser.getSelectedFile().getAbsolutePath();
				m.saveLastApkPath(path);
				pathText.setText(path);
			}
		}
		
	}
}
