/*
 * Copyright (C) 2017 Ecky Studio
 * Author:Ecky Leung(liangyi)
 * Creation Date:2017-1-23
 * Function:provide some usual methods about Screen,Layout to use easily
 */


package cn.eckystudio.m.file;

import java.io.File;

import cn.eckystudio.m.base.Action;

public class FileSystem {
	/***
	 * Enumerate files for specified diretorcy
	 * @param file target directory
	 * @param action
	 */
	public static void ergodicFile(File file,Action<String> action) {
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				action.run(f.getAbsolutePath());
			} else {
				ergodicFile(f,action);
			}
		}
	}

	public static void ergodicFile(String path,Action<String> action){
		File f = new File(path);
		if(!f.exists())
			return;

		ergodicFile(f,action);
	}

	/***
	 * 遍历目录下所有文件，带遍历深度
	 * @param file
	 * @param action 文件的动作
	 * @param depth 指定遍历深度，1为一级
	 */
	public static void ergodicFile(File file,Action<String> action,int depth){
		if(depth <= 0)
			return;

		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				action.run(f.getAbsolutePath());
			} else {
				ergodicFile(f,action,depth--);
			}
		}
	}

	public static void ergodicFile(String path,Action<String> action,int depth){
		File f = new File(path);
		if(!f.exists())
			return;

		ergodicFile(f,action,depth);
	}
}
