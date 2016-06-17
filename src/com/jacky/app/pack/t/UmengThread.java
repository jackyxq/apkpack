package com.jacky.app.pack.t;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jacky.app.pack.PackFrame;
import com.jacky.app.pack.SignInfo;
import com.jacky.app.pack.SignKeyDialog;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class UmengThread extends BaseThread {

	private static final String TAG = "${umeng_channel}";
	private ExecutorService executor;
	private CountDownLatch latch;
	private int AllSize;
	
	public UmengThread(PackFrame f) {
		super(f);
	}

	@Override
	boolean generateChannels(File oriFile, List<String> channels, File outputDir) {
		SignInfo sign = frame.getConfigManager().getSignInfo();
		if(sign == null) {
			dismissProcessDialog();
			new SignKeyDialog(frame).setVisible(true);
			return false;
		}
		
		try {
			ZipFile file = new ZipFile(oriFile);
			@SuppressWarnings("unchecked")
			List<FileHeader> l = file.getFileHeaders();
			//如果签名相同使用该方式
			for(FileHeader o : l) {
				if("AndroidManifest.xml".equals(o.getFileName())) {
					peelManifest(file.getInputStream(o));
					break;
				}
			}
			//签名文件不同需要先删除旧的签名文件
//			int size = l.size();
//			for(int i = 0;i < size; ) {
//				FileHeader h = l.get(i);
//				String name = h.getFileName();
//				if("AndroidManifest.xml".equals(name)) {
//					peelManifest(file.getInputStream(h));
//				} else if(name.startsWith("META-INF/")) {
//					file.removeFile(h);
//					i--;
//					size--;
//				}
//				i++;
//			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		AllSize = channels.size() + 1;
		int num = Runtime.getRuntime().availableProcessors();
		executor = Executors.newFixedThreadPool(num);
		latch = new CountDownLatch(AllSize - 1);
		
		for(String name : channels) {
			File f2 = new File(outputDir, oriFile.getName().replace(".apk", "_"+name+".apk"));
			System.out.println("copy : " + copyFileByChannel(oriFile, f2));
			
			try {
				ZipFile zipFile = new ZipFile(f2);
				ZipParameters parameters = new ZipParameters();
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
				parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); 
				zipFile.addFile(generateNewManifest(name), parameters);
				
				signApk(sign, f2.getAbsolutePath());
			} catch (ZipException e) {
				e.printStackTrace();
			} 
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdownNow();
		System.out.println("all finish");
		return true;
	}
	
	private void signApk(final SignInfo info, final String name) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				StringBuilder b = new StringBuilder(info.jdk);
				b.append("/bin/jarsigner.exe -keystore ").append(info.path)
					.append(" -signedjar ").append(name).append(" ").append(name).append(" ")
					.append(info.alias).append(" -storepass ").append(info.pwd)
					.append(" -sigalg SHA1withRSA -digestalg SHA1");
				
//				System.out.println(b.toString());
				try {
					Process p = Runtime.getRuntime().exec(b.toString());
					p.waitFor();					
					p.destroy();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				
				latch.countDown();
				dialog.setValue((int) ((AllSize - latch.getCount()) * 100 / AllSize));
			}
		});
	}
	
	/**
	 * 生成新的 Manifest文件
	 * @param name
	 * @return
	 */
	private File generateNewManifest(String name) {
		File file = new File("AndroidManifest.xml");
		OutputStream out = null;
		InputStream in = null;
		try{
			int c;
			byte[] b = new byte[100 * 1024];
			out = new FileOutputStream(file);
			
			in = new FileInputStream("manifest1");
			while((c = in.read(b)) != -1) {
				out.write(b, 0, c);
			}
			closeInputStream(in);
			
			int length = TAG.length();
			out.write(name.length());
			char[] newchar = Arrays.copyOf(name.toCharArray(), length);
			for(int i = 0;i < length;i++ ) {
				out.write(0);
				out.write(newchar[i]);
			}
			
			in = new FileInputStream("manifest2");
			while((c = in.read(b)) != -1) {
				out.write(b, 0, c);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeInputStream(in);
			closeOutputStream(out);
		}
		return file;
	}

	/**
	 * 将 Manifest文件从apk中剥离出来，并拆分为2个文件
	 * @param stream
	 */
	private void peelManifest(InputStream stream) {
		int tagLength = TAG.length();

		byte[] b = new byte[tagLength * 2];
		OutputStream out = null;
		try {
			out = new FileOutputStream("manifest1");
			while (true) {
				int c = stream.read();
				if(c == -1) break;
				
				if(c != tagLength) {
					out.write(c);
					continue;
				}
				
				c = stream.read(b);//读取TAG标识
				if(c == -1) break;
				
				boolean found = true;
				for(int i = 0;i < tagLength;i++) {
					if(b[i*2 + 1] != TAG.charAt(i)) {
						found = false;
						break;
					}
				}
				if(found) {
					closeOutputStream(out);
					out = new FileOutputStream("manifest2");
				} else {
					out.write(tagLength);
					out.write(b, 0, c);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeOutputStream(out);
			closeInputStream(stream);
		}//end try...catch...
	}
}
