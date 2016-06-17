package com.jacky.app.pack;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.jacky.app.pack.listener.AddQudaoListener;
import com.jacky.app.pack.listener.EscListener;
import com.jacky.app.pack.listener.FileChooserListener;
import com.jacky.app.pack.t.MetaInfThread;
import com.jacky.app.pack.t.UmengThread;
import com.jacky.app.pack.t.ZipCommentThread;
import com.jacky.app.pack.widget.CloseLabel;
import com.jacky.app.pack.widget.CloseLabel.OnCloseListener;
import com.jacky.app.pack.widget.LinkLabel;

@SuppressWarnings("serial")
public class PackFrame extends JFrame {

	private static final int TYPE = 3;
	
	private ConfigManager mManager;
	private JTextField mApkPath;
	private JPanel mQudaoPanel;
	
	PackFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}  
		mManager = new ConfigManager();
        
		setTitle("安卓渠道包打包工具");
		setSize(500, 420);
		setIconImage(new ImageIcon(getClass().getResource("/icon.jpg")).getImage());
		setLocationRelativeTo(null);
		setLayout(null);
		
		buildFrame();
	}
	
	private void buildFrame() {
		FileChooserListener fileListener = new FileChooserListener(this);
		EscListener listener = new EscListener(this);
		
		JLabel l = new JLabel("APK 路径：");
		l.setBounds(10, 10, 80, 25);
		add(l);
		
		mApkPath = new JTextField();
		mApkPath.setBounds(70, 10, 330, 25);
		mApkPath.setEditable(false);
		mApkPath.addMouseListener(fileListener);
		mApkPath.addKeyListener(listener);
		add(mApkPath);
		
		JButton b = new JButton("查找");
		b.setBounds(410, 10, 60, 25);
		b.addActionListener(fileListener);
		b.addKeyListener(listener);
		add(b);
		
		mQudaoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		mQudaoPanel.setPreferredSize(new Dimension(430, 1000)); //实现自动换行的功能
		
		JScrollPane mPane = new JScrollPane();
		mPane.setBounds(10, 50, 460, 250);
		mPane.setBorder(BorderFactory.createEtchedBorder());
		mPane.setViewportView(mQudaoPanel);
		add(mPane);
		
		
		JButton addBtn = new JButton("添加渠道");
		addBtn.setBounds(10, 350, 80, 35);
		addBtn.addActionListener(new AddQudaoListener(this));
		addBtn.addKeyListener(listener);
		add(addBtn);
		
		switch(TYPE) {
		case 3 :
			JButton keyBtn = new JButton("签名配置");
			keyBtn.setBounds(10, 310, 80, 35);
			keyBtn.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					new SignKeyDialog(PackFrame.this).setVisible(true);
				}
				
			});
			keyBtn.addKeyListener(listener);
			add(keyBtn);
			break;
		}
		
		JButton packBtn = new JButton("一键打包");
		packBtn.setBounds(200, 320, 100, 50);
		packBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(TYPE) {
				case 1 : 
					//打包方式一
					new MetaInfThread(PackFrame.this).start();
					break;
				case 2 : 
					//打包方式二（推荐）
					new ZipCommentThread(PackFrame.this).start();
					break;
				case 3 : 
					//打包方式三
					new UmengThread(PackFrame.this).start();
					break;
				}
			}
		});
		packBtn.addKeyListener(listener);
		add(packBtn);
		
		LinkLabel down = new LinkLabel("源码地址","https://github.com/jackyxq/apkpack");
		down.setBounds(440, 365, 80, 25);
		add(down);
		
		//文件拖拽功能
		new DropTarget(this, DnDConstants.ACTION_NONE, new MyDragGestureListener(), true);
		
		//渠道内容显示
		layoutPanel();
	}
	
	private void layoutPanel() {
		mQudaoPanel.removeAll();
		for(String name : mManager.getList()) {
			createLabel(name);
		}
		mQudaoPanel.validate();
	}
	
	private void createLabel(String l) {
		CloseLabel label = new CloseLabel(l);
		label.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public void onClose(CloseLabel e) {
				mManager.remove(e.getText());
				mQudaoPanel.remove(e);
				mQudaoPanel.repaint();
				mQudaoPanel.validate();
			}
		});	
		mQudaoPanel.add(label);
	}

	public void setApkPath(String path) {
		mApkPath.setText(path);
	}
	
	public void addQudao(String qudao) {
		mManager.add(qudao);
		createLabel(qudao);
		mQudaoPanel.validate();
	}
	
	public void saveData() {
		mManager.saveToConfigFile();
	}
	
	public ConfigManager getConfigManager() {
		return mManager;
	}
		
	public String getApkPath() {
		return mApkPath.getText();
	}
	
	private class MyDragGestureListener implements DropTargetListener {

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			dropdrag(dtde);
		}

		@Override
		public void dragExit(DropTargetEvent dte) {}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {}

		@Override
		public void drop(DropTargetDropEvent dtde) {}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {}
		
		private void dropdrag(DropTargetDragEvent dtde) {
			DataFlavor[] dataFlavors = dtde.getCurrentDataFlavors();  
	        if(dataFlavors[0].match(DataFlavor.javaFileListFlavor)){  
	            try {  
	                Transferable tr = dtde.getTransferable();  
	                Object obj = tr.getTransferData(DataFlavor.javaFileListFlavor);  
	                @SuppressWarnings("unchecked")
					List<File> files = (List<File>)obj;  
	                int size = files.size();
	                boolean found = false;
	                for(int i = 0; i < size;i++) {  
	                	String name = files.get(i).getAbsolutePath();
	                	if(name.endsWith(".apk")) {
	                		mApkPath.setText(name);
	                		mManager.saveLastApkPath(name);
	                		found = true;
	                		break;
	                	}
	                }  
	                
	                if(!found) {
	                	//TODO... 实现类似android toast的功能
	                	System.out.println("不支持该文件格式");
	                }
	            } catch (Exception ex) {  
	            	ex.printStackTrace();
	            }  
	        }//end if        
	        
		}
	}
}
