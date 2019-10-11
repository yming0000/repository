package repository.main;

import repository.util.FileDownloadUtil;

public class FileDownload {

	public static void main(String[] args) throws Exception {

		String url = "";// download url
		String path = "";// local path
		String name = "";// file name, optional

		FileDownloadUtil.fileDownload(url, path);
		FileDownloadUtil.fileDownload(url, path, name);

	}

}
