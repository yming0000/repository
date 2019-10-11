package repository.util;

import java.io.File;

public class FileUtil {

	public static boolean judeDirExists(String filePath) {
		File file = new File(filePath);
		return judeDirExists(file);
	}

	public static boolean judeDirExists(File file) {
		boolean b = false;
		if (file.exists()) {
			if (file.isDirectory()) {
				System.out.println("dir exists");
			} else {
				System.out.println("the same name file exists, can not create dir");
			}
		} else {
			System.out.println("dir not exists, create it ...");
			b = file.mkdirs();
		}
		return b;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			File filePath = new File(folderPath);
			filePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void delFile(String path) {
		try {
			File file = new File(path);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean delAllFile(String path) {
		boolean flag = false;
		try {
			File file = new File(path);
			if (!file.exists()) {
				return flag;
			}
			if (!file.isDirectory()) {
				return flag;
			}
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					delAllFile(path + File.separator + tempList[i]);// 先删除文件夹里面的文件
					delFolder(path + File.separator + tempList[i]);// 再删除空文件夹
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
