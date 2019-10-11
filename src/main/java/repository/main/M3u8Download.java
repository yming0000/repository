package repository.main;

import repository.util.M3u8Util;

public class M3u8Download {

	public static void main(String[] args) throws Exception {

		String originUrlpath = "";// download url
		String folderPath = "";// local path
		String fileName = "";// file name, optional
		boolean threadFlag = false;// is multi-thread

		M3u8Util m;
		m = new M3u8Util(originUrlpath, folderPath);
		m = new M3u8Util(originUrlpath, folderPath, threadFlag);
		m = new M3u8Util(originUrlpath, folderPath, fileName);
		m = new M3u8Util(originUrlpath, folderPath, fileName, threadFlag);
		m.download();

	}

}
