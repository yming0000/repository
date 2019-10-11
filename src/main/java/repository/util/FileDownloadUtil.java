package repository.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloadUtil {

	public static void fileDownload(String url, String path) throws Exception {
		String p = new URL(url).getFile();
		String names[] = p.split("/");
		String name = names[names.length - 1];
		fileDownload(url, path, name);
	}

	public static void fileDownload(String url, String path, String name) throws Exception {
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		BufferedInputStream bis = null;
		FileOutputStream out = null;
		File file0 = new File(path);
		if (!file0.isDirectory() && !file0.exists()) {
			file0.mkdirs();
		}
		out = new FileOutputStream(file0 + File.separator + name);
		// ��������
		URL httpUrl = new URL(url);
		conn = (HttpURLConnection) httpUrl.openConnection();
		// ��Post��ʽ�ύ����Ĭ��get��ʽ
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		// post��ʽ����ʹ�û���
		conn.setUseCaches(false);
		// ����ָ������Դ
		conn.connect();
		// ��ȡ����������
		inputStream = conn.getInputStream();
		bis = new BufferedInputStream(inputStream);
		byte b[] = new byte[1024];
		int len = 0;
		while ((len = bis.read(b)) != -1) {
			out.write(b, 0, len);
		}
		if (out != null) {
			out.close();
		}
		if (bis != null) {
			bis.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}

}
