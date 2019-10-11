package repository.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3u8Util {

	private String originUrlpath;

	private String folderPath;

	private String folderPathTemp;

	private String fileName;

	private boolean threadFlag;

	private HashMap<Integer, String> map = new HashMap<Integer, String>();

	public M3u8Util(String originUrlpath, String folderPath) {
		this(originUrlpath, folderPath, true);
	}

	public M3u8Util(String originUrlpath, String folderPath, boolean threadFlag) {
		this(originUrlpath, folderPath, System.currentTimeMillis() + ".mp4", threadFlag);
		try {
			String p = new URL(originUrlpath).getFile();
			String names[] = p.split("/");
			String name = names[names.length - 1];
			name = name.contains(".") ? name.substring(0, name.lastIndexOf(".")) : name;
			name += ".mp4";
			this.fileName = System.currentTimeMillis() + "-" + name;
		} catch (Exception e) {
		}
	}

	public M3u8Util(String originUrlpath, String folderPath, String fileName) {
		this(originUrlpath, folderPath, fileName, true);
	}

	public M3u8Util(String originUrlpath, String folderPath, String fileName, boolean threadFlag) {
		this.originUrlpath = originUrlpath;
		this.folderPath = folderPath;
		this.fileName = fileName;
		this.threadFlag = threadFlag;
		this.folderPathTemp = folderPath + File.separator + UUID.randomUUID();
	}

	public void download() throws Exception {
		FileUtil.judeDirExists(folderPathTemp);
		String content = getIndexFile();
		List<String> list = analysisIndex(content);
		System.out.println("索引文件下载解析完毕，开始下载分片资源...");
		if (!threadFlag)
			// 单线程下载
			downLoadIndexFile(list);
		else
			// 多线程下载
			downLoadIndexFileAsync(list);
	}

	// 下载索引文件
	private String getIndexFile() throws Exception {
		URL url = new URL(originUrlpath);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		String content = "";
		String line;
		while ((line = in.readLine()) != null)
			content += line + "\n";
		in.close();
		return content;
	}

	// 解析索引文件
	private List<String> analysisIndex(String content) throws Exception {
		Pattern pattern = Pattern.compile(".*ts");
		Matcher ma = pattern.matcher(content);
		List<String> list = new ArrayList<String>();
		while (ma.find())
			list.add(ma.group());
		return list;
	}

	// 下载视频片段
	// 同步下载
	private void downLoadIndexFile(List<String> urlList) throws Exception {
		for (int i = 0; i < urlList.size(); i++) {
			String subUrlPath = urlList.get(i);
			String fileOutPath = folderPathTemp + File.separator + i + ".ts";
			map.put(i, fileOutPath);
			try {
				downloadNet(subUrlPath, fileOutPath);
				System.out.println("下载进度：" + (i + 1) + "/" + urlList.size());
			} catch (Exception e) {
				System.err.println("错误告警ERROR--分片资源下载出错--" + (i + 1));
			}
		}
		System.out.println("分片资源下载完毕，开始视频片段合成...");
		composeFile();
	}

	// 多线程下载
	private void downLoadIndexFileAsync(final List<String> urlList) throws Exception {
		ExecutorsUtil executors = new ExecutorsUtil();
		for (int i = 0; i < urlList.size(); i++) {
			final String subUrlPath = urlList.get(i);
			final String fileOutPath = folderPathTemp + File.separator + i + ".ts";
			map.put(i, fileOutPath);
			final int icounting = i;
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						downloadNet(subUrlPath, fileOutPath);
						System.out.println("下载进度：" + (icounting + 1) + "/" + urlList.size());
					} catch (Exception e) {
						System.err.println("错误告警ERROR--分片资源下载出错--" + (icounting + 1));
					}
				}
			});
		}
		executors.shutdown(new Runnable() {
			@Override
			public void run() {
				System.out.println("分片资源下载完毕，开始视频片段合成...");
				composeFile();
			}
		});
	}

	// 文件下载
	private void downloadNet(String fullUrlPath, String fileOutPath) throws Exception {
		int byteread = 0;
		URL url = new URL(fullUrlPath);
		URLConnection conn = url.openConnection();
		InputStream inStream = conn.getInputStream();
		FileOutputStream fs = new FileOutputStream(fileOutPath);
		byte[] buffer = new byte[1204];
		while ((byteread = inStream.read(buffer)) != -1)
			fs.write(buffer, 0, byteread);
		fs.close();
	}

	// 视频片段合成
	private void composeFile() {
		try {
			String fileOutPath = folderPath + File.separator + fileName;
			FileOutputStream fileOutputStream = new FileOutputStream(new File(fileOutPath));
			byte[] bytes = new byte[1024];
			int length = 0;
			for (int i = 0; i < map.size(); i++) {
				String nodePath = map.get(i);
				File file = new File(nodePath);
				if (!file.exists())
					continue;
				FileInputStream fis = new FileInputStream(file);
				while ((length = fis.read(bytes)) != -1) {
					fileOutputStream.write(bytes, 0, length);
				}
				fis.close();
			}
			fileOutputStream.close();
			System.out.println("视频片段合成完毕，开始清理临时文件...");
		} catch (Exception e) {
			System.out.println("视频片段合成出错...");
			e.printStackTrace();
		}
		delFolder();
	}

	private void delFolder() {
		FileUtil.delFolder(folderPathTemp);
		System.out.println("清理临时文件完毕...");
	}

}
