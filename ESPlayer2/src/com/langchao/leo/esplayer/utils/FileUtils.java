package com.langchao.leo.esplayer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

/**
 * file操作类: <br>
 * 1、判断文件是否存在 <br>
 * 2、获取当前文件路径 <br>
 * 3、检测sd卡是否可用 <br>
 * 4、检测sd卡剩余空间 <br>
 * 5、将图片文件移至相册 <br>
 * 6、判断文件是否是Gif或者png、gpg <br>
 * 7、获取缓存文件夹路径 <br>
 * 8、在SDCard的根目录、或缓存文件夹下创建临时文件 <br>
 * 9、创建文件 <br>
 * 10、清除缓存 <br>
 * 11、获取某个文件 <br>
 * 12、把字节数组保存为一个文件 <br>
 * 13、把Bitmap转Byte <br>
 * 14、删除文件 <br>
 * 15、得到一个文件的大小
 * @author fengmiao
 * 
 * <h1>data/data/files文件夹路径:context.getFilesDir()</h1>
 * 		<h2>获取SD卡状态:Environment.getExternalStorageState()</h1>
 * 		<h3>SD卡根目录:Environment.getExternalStorageDirectory()</h2>
 */
public class FileUtils {

	private static final int IO_BUFFER_SIZE = 16384;

	/**
	 * 创建文件
	 * 
	 * @param absPath
	 *            绝对路径
	 */
	public static boolean create(String absPath) {
		return create(absPath, false);
	}

	
	/**
	 * 创建文件
	 * @param absPath
	 * @param force
	 * @return
	 */
	public static boolean create(String absPath, boolean force) {
		if (TextUtils.isEmpty(absPath)) {
			return false;
		}

		if (exists(absPath)) {
			return true;
		}

		String parentPath = getParent(absPath);
		mkdirs(parentPath, force);

		try {
			File file = new File(absPath);
			return file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断文件是否存在
	 * @param file
	 * @return
	 */
	public static boolean exists(String absPath) {
		if (TextUtils.isEmpty(absPath)) {
			return false;
		}
		File file = new File(absPath);
		return exists(file);
	}

	
	/**
	 * 判断文件是否存在
	 * @param file
	 * @return
	 */
	public static boolean exists(File file) {
		if (file == null) {
			return false;
		}
		return file.exists();
	}

	
	/**
	 *  ？ 判断文件夹是否存在并创建
	 * @param absPath
	 * @return
	 */
	public static boolean mkdirs(String absPath) {
		return mkdirs(absPath, false);
	}

	/**
	 * ？ 判断文件夹是否存在并创建
	 * @param absPath
	 * @param force
	 * @return
	 */
	public static boolean mkdirs(String absPath, boolean force) {
		File file = new File(absPath);
		if (exists(absPath) && !isFolder(absPath)) {
			if (!force) {
				return false;
			} else {
				delete(file);
			}
		}
		try {
			file.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exists(file);
	}

	
	/**
	 * 文件剪贴
	 * @param srcPath
	 * @param dstPath
	 * @return
	 */
	public static boolean move(String srcPath, String dstPath) {
		return move(srcPath, dstPath, false);
	}

	
	/**
	 * 文件剪贴
	 * @param srcPath
	 * @param dstPath
	 * @param force
	 * @return
	 */
	public static boolean move(String srcPath, String dstPath, boolean force) {
		if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
			return false;
		}

		if (!exists(srcPath)) {
			return false;
		}

		if (exists(dstPath)) {
			if (!force) {
				return false;
			} else {
				delete(dstPath);
			}
		}

		try {
			File srcFile = new File(srcPath);
			File dstFile = new File(dstPath);
			return srcFile.renameTo(dstFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * 删除文件或者删除目录下所有文件
	 * @param file
	 * @return
	 */
	public static boolean delete(String absPath) {
		if (TextUtils.isEmpty(absPath)) {
			return false;
		}

		File file = new File(absPath);
		return delete(file);
	}

	
	/**
	 * 删除文件或者删除目录下所有文件
	 * @param file
	 * @return
	 */
	public static boolean delete(File file) {
		if (!exists(file)) {
			return true;
		}

		if (file.isFile()) {
			return file.delete();
		}

		boolean result = true;
		File files[] = file.listFiles();
		for (int index = 0; index < files.length; index++) {
			result |= delete(files[index]);
		}
		result |= file.delete();

		return result;
	}

	
	

	
	/**
	 * 判断childPath是否是在parentPath目录下
	 * @param childPath
	 * @param parentPath
	 * @return
	 */
	public static boolean childOf(String childPath, String parentPath) {
		if (TextUtils.isEmpty(childPath) || TextUtils.isEmpty(parentPath)) {
			return false;
		}
		childPath = cleanPath(childPath);
		parentPath = cleanPath(parentPath);
		if (childPath.startsWith(parentPath + File.separator)) {
			return true;
		}
		return false;
	}

	
	/***
	 * 当前文件夹下文件数量
	 * @param absPath
	 * @return
	 */
	public static int childCount(String absPath) {
		if (!exists(absPath)) {
			return 0;
		}
		File file = new File(absPath);
		File[] children = file.listFiles();
		if (children == null || children.length == 0) {
			return 0;
		}
		return children.length;
	}

	/**
	 * 获取文件绝对路径:
	 * <p>
	 * getAbsolutePath(): 返回的是定义时的路径对应的相对路径，但不会处理“.”和“..”的情况 
	 * <p>getCanonicalPath():
	 * 返回的是规范化的绝对路径，相当于将getAbsolutePath()中的“.”和“..”解析成对应的正确的路径
	 * 
	 * @param absPath
	 * @return
	 */
	public static String cleanPath(String absPath) {
		if (TextUtils.isEmpty(absPath)) {
			return absPath;
		}
		try {
			File file = new File(absPath);
			absPath = file.getCanonicalPath();
		} catch (Exception e) {

		}
		return absPath;
	}

	/**
	 * 获取当前文件或者目录下所有文件的大小
	 * @param absPath
	 * @return
	 */
	public static long size(String absPath) {
		if (absPath == null) {
			return 0;
		}
		File file = new File(absPath);
		return size(file);
	}

	
	/**
	 * 获取当前文件或者目录下所有文件的大小
	 * @param absPath
	 * @return
	 */
	public static long size(File file) {
		if (!exists(file)) {
			return 0;
		}

		long length = 0;
		if (isFile(file)) {
			length = file.length();
			return length;
		}

		File files[] = file.listFiles();
		if (files == null || files.length == 0) {
			return length;
		}

		int size = files.length;
		for (int index = 0; index < size; index++) {
			File child = files[index];
			length += size(child);
		}
		return length;
	}

	
	/**
	 * 文件复制
	 * @param srcPath
	 * @param dstPath
	 * @return
	 */
	public static boolean copy(String srcPath, String dstPath) {
		return copy(srcPath, dstPath, false);
	}

	
	/**
	 * 文件复制
	 * @param srcPath
	 * @param dstPath
	 * @param force
	 * @return
	 */
	public static boolean copy(String srcPath, String dstPath, boolean force) {
		if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
			return false;
		}

		// check if copy source equals destination
		if (srcPath.equals(dstPath)) {
			return true;
		}

		// check if source file exists or is a directory
		if (!exists(srcPath) || !isFile(srcPath)) {
			return false;
		}

		// delete old content
		if (exists(dstPath)) {
			if (!force) {
				return false;
			} else {
				delete(dstPath);
			}
		}
		if (!create(dstPath)) {
			return false;
		}

		FileInputStream in = null;
		FileOutputStream out = null;

		// get streams
		try {
			in = new FileInputStream(srcPath);
			out = new FileOutputStream(dstPath);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		try {
			byte[] buffer = new byte[IO_BUFFER_SIZE];

			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 根据路径判断是否是文件
	 * @param absPath
	 * @return
	 */
	public final static boolean isFile(String absPath) {
		boolean exists = exists(absPath);
		if (!exists) {
			return false;
		}

		File file = new File(absPath);
		return isFile(file);
	}

	
	/**
	 * 根据file判断是否是文件
	 * @param file
	 * @return
	 */
	public final static boolean isFile(File file) {
		if (file == null) {
			return false;
		}

		return file.isFile();
	}

	/**
	 * 判断是否是目录
	 * @param absPath
	 * @return
	 */
	public final static boolean isFolder(String absPath) {
		boolean exists = exists(absPath);
		if (!exists) {
			return false;
		}

		File file = new File(absPath);
		return file.isDirectory();
	}

	/**
	 * 获取文件名称
	 * @param file
	 * @return
	 */
	public final static String getName(File file) {
		if (file == null) {
			return null;
		} else {
			return getName(file.getAbsolutePath());
		}
	}

	/**
	 * 获取文件名
	 * @param absPath 
	 * @return  menu.jpg
	 */
	public final static String getName(String absPath) {
		if (TextUtils.isEmpty(absPath)) {
			return absPath;
		}

		String fileName = null;
		int index = absPath.lastIndexOf("/");
		if (index > 0 && index < (absPath.length() - 1)) {
			fileName = absPath.substring(index + 1, absPath.length());
		}
		return fileName;
	}

	
	/**
	 * 获取文件的父目录
	 * @param file
	 * @return
	 */
	public final static String getParent(File file) {
		if (file == null) {
			return null;
		} else {
			return file.getParent();
		}
	}
	
	/**
	 * 获取文件的父目录
	 * @param absPath
	 * @return
	 */
	public final static String getParent(String absPath) {
		if (TextUtils.isEmpty(absPath)) {
			return null;
		}
		absPath = cleanPath(absPath);
		File file = new File(absPath);
		return getParent(file);
	}

	/**
	 * 根据文件名获取文件前缀
	 * 
	 * @param file
	 * @return
	 */
	public static String getStem(File file) {
		if (file == null) {
			return null;
		}
		return getStem(file.getName());
	}

	/**
	 * 根据文件名获取文件前缀
	 * 
	 * @param fileName
	 *            eg: /data/android/packageName/cache/89890203.txt
	 * @return eg:/data/android/packageName/cache/89890203
	 */
	public final static String getStem(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return null;
		}

		int index = fileName.lastIndexOf(".");
		if (index > 0) {
			return fileName.substring(0, index);
		} else {
			return "";
		}
	}

	/**
	 * 截取文件后缀
	 * 
	 * @param file
	 * @return
	 */
	public static String getExtension(File file) {
		if (file == null) {
			return null;
		}
		return getExtension(file.getName());
	}

	/**
	 * 根据文件名截取后缀
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return "";
		}

		int index = fileName.lastIndexOf('.');
		if (index < 0 || index >= (fileName.length() - 1)) {
			return "";
		}
		return fileName.substring(index + 1);
	}

	/**
	 * ？ 获取文件类型
	 */
	public static String getMimeType(File file) {
		if (file == null) {
			return "*/*";
		}
		String fileName = file.getName();
		return getMimeType(fileName);
	}

	/**
	 * ？ 获取文件类型
	 */
	public static String getMimeType(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return "*/*";
		}
		String extension = getExtension(fileName);
		MimeTypeMap map = MimeTypeMap.getSingleton();
		String type = map.getMimeTypeFromExtension(extension);
		if (TextUtils.isEmpty(type)) {
			return "*/*";
		} else {
			return type;
		}
	}
	
	/**
	 * 判断文件是否为图片文件(GIF)
	 * 
	 * @param srcFileName
	 * @return
	 */
	public static boolean isGifImage(File srcFilePath) {
		FileInputStream imgFile = null;
		byte[] b = new byte[10];
		int l = -1;
		try {
			imgFile = new FileInputStream(srcFilePath);
			l = imgFile.read(b);
			imgFile.close();
		} catch (Exception e) {
			return false;
		}

		if (l == 10) {
			byte b0 = b[0];
			byte b1 = b[1];
			byte b2 = b[2];
			byte b3 = b[3];
			byte b6 = b[6];
			byte b7 = b[7];
			byte b8 = b[8];
			byte b9 = b[9];

			if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
				return true;
			} else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G') {
				return false;
			} else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I'
					&& b9 == (byte) 'F') {
				return false;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
}
