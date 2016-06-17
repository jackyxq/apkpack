package com.jacky.app.pack.t;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jacky.app.pack.PackFrame;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * 根据 <a href="#">http://tech.meituan.com/mt-apk-packaging.html</a> 这文章来实现渠道包的生成
 * @author jacky
 *
 */
public class MetaInfThread extends BaseThread {
	
	public MetaInfThread(PackFrame f) {
		super(f);
	}
	
	private void addChannel(File file, File addFile, String name) {
		try {
			addFile.createNewFile();
			
			ZipFile zipFile = new ZipFile(file);
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); 
			parameters.setRootFolderInZip("META-INF");
			zipFile.addFile(addFile, parameters);
		} catch (ZipException | IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	boolean generateChannels(File oriFile, List<String> channels, File outputDir) {
		File meta = new File("META-INF");
		meta.mkdir();
		
		int size = channels.size(), i = 0;
		for(String name : channels) {
			dialog.setValue(i * 100 / size);
			
			File f2 = new File(outputDir, oriFile.getName().replace(".apk", "_"+name+".apk"));
			f2.delete();
			System.out.println("copy : " + copyFileByChannel(oriFile, f2));
			
			File af = new File(meta, "ch_" + name);
			addChannel(f2, af, name);
			i++;				
		}
		return true;
	}
	
}
