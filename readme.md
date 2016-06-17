# ApkPackageTool
Android channel package tool （安卓渠道打包工具）

# 该打包使用了3种打包方式
	1、参考美图的方式，在META-INF目录下增加一个标识文件来标识渠道名称
	2、根据zip的特性，往zip comment中写入渠道名称
	3、在AndroidManifest.xml中添加 meta元素的方式来标识渠道，同时又避免了应用的反编译与编译减少打包时间及不同apktool版本冲突问题