package com.http;
/**
 * 
 * @author leovo
 *
 */
public class Main {
	public static void main(String[] args) {
		HttpServer httpServer = new HttpServer(80);
		httpServer.start();
	}
}