package com.joe.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.main.task.QRIdTask;

public class IOUtils {
	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

	/**
	 * 将流中的数据读取为字符串
	 * 
	 * @param in
	 *            输入流
	 * @param charset
	 *            字符串编码
	 * @return 流中的数据
	 * @throws IOException
	 */
	public static String read(InputStream in, String charset) throws IOException {
		charset = charset == null ? "UTF8" : charset;
		StringBuilder sb = new StringBuilder();
		int len = 0;
		byte[] buffer = new byte[256];
		while ((in.read(buffer, 0, buffer.length)) != -1) {
			sb.append(new String(buffer, 0, len, charset));
		}
		return sb.toString();
	}

	/**
	 * 将流中的数据以UTF8编码形式读取为字符串
	 * 
	 * @param in
	 *            输入流
	 * @return 流中的数据
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException {
		return read(in, null);
	}

	/**
	 * 将输入流中的东西保存到本地
	 * 
	 * @param path
	 *            保存路径
	 * @param name
	 *            文件名
	 * @param input
	 *            输入流
	 * @throws IOException
	 */
	public static void saveAsFile(String path, String name, InputStream input) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(path + name));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = input.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			logger.info("文件已经保存到本地，保存位置：{}", path);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

}
