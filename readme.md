# ApkPackageTool
Android channel package tool （安卓渠道打包工具）

该打包使用了3种打包方式
---------------------------
	1、参考美图的方式，在META-INF目录下增加一个标识文件来标识渠道名称
	2、根据zip的特性，往zip comment中写入渠道名称
	3、在AndroidManifest.xml中添加 meta元素的方式来标识渠道，
	同时又避免了应用的反编译与编译减少打包时间及不同apktool版本冲突问题
	

项目介绍
---------------------------
	ApkPack_*.jar 分别对应不同打包方式的可执行文件，运行可执行文件需要安装 jdk，建议安装jdk8。打包效率方式2最快，方式3最慢，如果项目中应用了友盟的统计功能，建议使用方式3
	
Android代码获取渠道名称的方式也各有不同
---------------------------
###  方式1
	public static String getMetaInfChannel(Context context) {
		ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/ch_")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int ind = ret.indexOf('_');
        if (ind == -1) {
        	return "website"; //默认的渠道名称
        } else {
        	return ret.substring(ind + 1);
        }
	}
	
###  方式2 （需要使用到第三方包zip4j，在libs目录下）
	@SuppressLint("NewApi")
	public static String getCommentChannel(Context context) {
		ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	ZipFile zipfile = null;
            try {
                	zipfile = new ZipFile(sourceDir);
                	ret = zipfile.getComment();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (zipfile != null) {
                    try {
                        zipfile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
        	ret = readComment(sourceDir);
        }

        if(ret == null) {
        	return "website"; //默认的渠道名称
        } else {
        	return ret;
        }
	}

	private static String readComment(String f) {
		net.lingala.zip4j.core.ZipFile zipfile = null;
		try {
			zipfile = new net.lingala.zip4j.core.ZipFile(f);
			return zipfile.getComment();
		} catch (ZipException e) {
			e.printStackTrace();
		}
		return null;
	}
###  方式3
	public static String getManifestChannel(Context context) {
		try {
			String name = context.getPackageName();
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(name, PackageManager.GET_META_DATA);
			return info.metaData.getString("umeng_channel");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "website";
	}
