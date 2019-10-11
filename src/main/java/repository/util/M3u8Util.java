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
		System.out.println("�����ļ����ؽ�����ϣ���ʼ���ط�Ƭ��Դ...");
		if (!threadFlag)
			// ���߳�����
			downLoadIndexFile(list);
		else
			// ���߳�����
			downLoadIndexFileAsync(list);
	}

	// ���������ļ�
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

	// ���������ļ�
	private List<String> analysisIndex(String content) throws Exception {
		Pattern pattern = Pattern.compile(".*ts");
		Matcher ma = pattern.matcher(content);
		List<String> list = new ArrayList<String>();
		while (ma.find())
			list.add(ma.group());
		return list;
	}

	// ������ƵƬ��
	// ͬ������
	private void downLoadIndexFile(List<String> urlList) throws Exception {
		for (int i = 0; i < urlList.size(); i++) {
			String subUrlPath = urlList.get(i);
			String fileOutPath = folderPathTemp + File.separator + i + ".ts";
			map.put(i, fileOutPath);
			try {
				downloadNet(subUrlPath, fileOutPath);
				System.out.println("���ؽ��ȣ�" + (i + 1) + "/" + urlList.size());
			} catch (Exception e) {
				System.err.println("����澯ERROR--��Ƭ��Դ���س���--" + (i + 1));
			}
		}
		System.out.println("��Ƭ��Դ������ϣ���ʼ��ƵƬ�κϳ�...");
		composeFile();
	}

	// ���߳�����
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
						System.out.println("���ؽ��ȣ�" + (icounting + 1) + "/" + urlList.size());
					} catch (Exception e) {
						System.err.println("����澯ERROR--��Ƭ��Դ���س���--" + (icounting + 1));
					}
				}
			});
		}
		executors.shutdown(new Runnable() {
			@Override
			public void run() {
				System.out.println("��Ƭ��Դ������ϣ���ʼ��ƵƬ�κϳ�...");
				composeFile();
			}
		});
	}

	// �ļ�����
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

	// ��ƵƬ�κϳ�
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
			System.out.println("��ƵƬ�κϳ���ϣ���ʼ������ʱ�ļ�...");
		} catch (Exception e) {
			System.out.println("��ƵƬ�κϳɳ���...");
			e.printStackTrace();
		}
		delFolder();
	}

	private void delFolder() {
		FileUtil.delFolder(folderPathTemp);
		System.out.println("������ʱ�ļ����...");
	}

}
