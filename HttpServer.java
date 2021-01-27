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
	 * ����������
	 */
	private ServerSocket serverSocket;
	/**
	 * ��־λ
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
		while (isRunning) {// һֱ������ֱ���ܵ�ֹͣ������
			Socket socket = null;
			try {
				// ���û�����󣬻�һֱhold������ȴ����пͻ��������ʱ��Ż��������ִ��
				socket = serverSocket.accept();
				// log
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));// ��ȡ������(����)
				int v_i = 0; // ����Ϊ״̬�н����ַ�����ȡ
				int result = 0;
				HashMap<String, String> map = null;
				String line = null;
				while ((line = bufferedReader.readLine()) != null && !line.equals("")) {// �õ���������ݣ�ע�������������жϷǿպ�""��Ҫ��ֻ�ж�null��������
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
				PrintStream printWriter = new PrintStream(socket.getOutputStream(), true);// ����ڶ���������ʾ�Զ�ˢ�»���

				doEcho(printWriter, result);// ������������
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
	 * ��������д�ؿͻ���
	 * 
	 * @param printStream
	 * @param record
	 */
	private void doEcho(PrintStream printStream, int result) {
//		printWriter.println("��������!!");
		printStream.println("HTTP/1.1 200 OK");
		printStream.println("Content-Type:text/html;charset:GBK");
		printStream.println();
		// �����ҳ��Ϣ
		printStream.println("<HTML><BODY>" + "<center>" + "<h1>HTTPЭ�������,��ǰʱ�䣺" + new java.util.Date() + "</h1>"
				+ "���ύ�����ݼ���������:<pre>" + result + "</pre>" + "</center></BODY></HTML>");
		printStream.flush();
		printStream.close();
	}

}
