package com.jacky.app.pack.t;

import java.io.File;
import java.util.List;

import com.jacky.app.pack.PackFrame;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * 根据ZIP具有注释的特性，根据注释来标识渠道
 * @author jacky
 *
 */
public class ZipCommentThread extends BaseThread {

	public ZipCommentThread(PackFrame f) {
		super(f);
	}

	@Override
	boolean generateChannels(File oriFile, List<String> channels, File outputDir) {
		int size = channels.size(), i = 0;
		for(String name : channels) {
			dialog.setValue(i * 100 / size);
			
			File f2 = new File(outputDir, oriFile.getName().replace(".apk", "_"+name+".apk"));
			System.out.println("copy : " + copyFileByChannel(oriFile, f2));
			
			try {
				ZipFile zipFile = new ZipFile(f2);
				zipFile.setComment(name);
			} catch (ZipException e) {
				e.printStackTrace();
			} 
			i++;				
		}
		return true;
	}

}
