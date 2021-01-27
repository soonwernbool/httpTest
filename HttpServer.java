package com.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * 
 * @author leovo
 *
 */
public class HttpServer implements Runnable {

	/**
	 * 服务器监听
	 */
	private ServerSocket serverSocket;
	/**
	 * 标志位
	 */
	private boolean isRunning;

	public HttpServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() {
		this.isRunning = true;

		new Thread(this).start();
	}

	@Override
	public void run() {
		while (isRunning) {// 一直监听，直到受到停止的命令
			Socket socket = null;
			try {
				// 如果没有请求，会一直hold在这里等待，有客户端请求的时候才会继续往下执行
				socket = serverSocket.accept();
				// log
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));// 获取输入流(请求)
				int v_i = 0; // 控制为状态行进入字符串截取
				int result = 0;
				HashMap<String, String> map = null;
				String line = null;
				while ((line = bufferedReader.readLine()) != null && !line.equals("")) {// 得到请求的内容，注意这里作两个判断非空和""都要，只判断null会有问题
					System.out.println(line);
					int if_no2 = line.indexOf("?");
					if (if_no2 > 0) {
						if (v_i == 0) {
							int i1 = line.indexOf("/");
							int i2 = line.indexOf("?");
							String mod = line.substring(i1 + 1, i2);
							int index_a = line.indexOf("=");
							int index_and = line.indexOf("&");
							int res_a = Integer.valueOf(line.substring(index_a + 1, index_and));
							int index_b = line.lastIndexOf("=");
							int index_sep = line.lastIndexOf(" ");
							int res_b = Integer.valueOf(line.substring(index_b + 1, index_sep));
							if ("add".equals(mod)) {
								result = res_a + res_b;
							}
							if ("mult".equals(mod)) {
								result = res_a * res_b;
							}
						}

					}
					v_i++;
				}
				// echo
				PrintStream printWriter = new PrintStream(socket.getOutputStream(), true);// 这里第二个参数表示自动刷新缓存

				doEcho(printWriter, result);// 将输出到浏览器
				// release
				printWriter.close();
				bufferedReader.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将计算结果写回客户端
	 * 
	 * @param printStream
	 * @param record
	 */
	private void doEcho(PrintStream printStream, int result) {
//		printWriter.println("浏览器你好!!");
		printStream.println("HTTP/1.1 200 OK");
		printStream.println("Content-Type:text/html;charset:GBK");
		printStream.println();
		// 输出主页信息
		printStream.println("<HTML><BODY>" + "<center>" + "<h1>HTTP协议服务器,当前时间：" + new java.util.Date() + "</h1>"
				+ "您提交的数据计算结果如下:<pre>" + result + "</pre>" + "</center></BODY></HTML>");
		printStream.flush();
		printStream.close();
	}

}
