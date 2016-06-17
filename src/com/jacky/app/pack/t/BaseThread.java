package com.jacky.app.pack.t;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.swing.JOptionPane;

import com.jacky.app.pack.PackFrame;
import com.jacky.app.pack.ProcessDialog;

public abstract class BaseThread extends Thread {

	PackFrame frame;
	ProcessDialog dialog;
	
	BaseThread(PackFrame f) {
		frame = f;
		dialog = new ProcessDialog(f);
	}
	
	public void showProcessDialog() {
		dialog.setVisible(true);
	}
	
	public void dismissProcessDialog() {
		dialog.setVisible(false);
	}

	@Override
	public synchronized void start() {
		super.start();
		showProcessDialog();
	}
	
	/**
	 * 生成渠道文件
	 * @param oriFile 原始文件
	 * @param channels 渠道名称
	 * @param outputDir 生成的渠道文件所在的目录
	 * @return 渠道生成成功则为true， 失败则为false
	 */
	abstract boolean generateChannels(File oriFile, List<String> channels, File outputDir);
	
	@Override
	public final void run() {
		long start = System.currentTimeMillis();
		String oriFile = frame.getApkPath();
		if(oriFile == null || "".equals(oriFile)) {
			dismissProcessDialog();
			JOptionPane.showMessageDialog(dialog, "APK路径不能为空");
			return;
		}
		
		File f1 = new File(oriFile);
		File parent = new File("output");
		parent.mkdir();
		
		if(!generateChannels(f1, frame.getConfigManager().getList(), parent)) {
			return;
		}
		dismissProcessDialog();
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		
		int result = JOptionPane.showConfirmDialog(frame, "打包完成，是否打开文件所在目录？","", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.OK_OPTION) {
			try {
				Desktop.getDesktop().open(parent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 传统的文件拷贝方式
	 * @param f1
	 * @param f2
	 * @return
	 * @throws Exception
	 */
	public static long copyFile(File f1,File f2) throws Exception {
		long time = System.currentTimeMillis();
		int length = 2097152;
		FileInputStream in = new FileInputStream(f1);
		FileOutputStream out = new FileOutputStream(f2);
		byte[] buffer = new byte[length];
		while (true) {
			int ins = in.read(buffer);
			if (ins == -1) {
				in.close();
				out.flush();
				out.close();
				return System.currentTimeMillis() - time;
			} else
				out.write(buffer, 0, ins);
		}
	}
	
	/**
	 * 通过通道来拷贝文件，效率比传统方式快
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static long copyFileByChannel(File f1, File f2) {
		long time = System.currentTimeMillis();
		
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			f2.createNewFile();
			
			in = new FileInputStream(f1);
			out = new FileOutputStream(f2);
			FileChannel inC = in.getChannel();
			FileChannel outC = out.getChannel();
			inC.transferTo(0, inC.size(), outC);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			inC.close();
//			outC.close();
			closeInputStream(in);
			closeOutputStream(out);
		}
		return System.currentTimeMillis() - time;
	}
	
	public static void closeInputStream(InputStream s) {
		if(s != null) {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void closeOutputStream(OutputStream o) {
		if(o != null) {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
