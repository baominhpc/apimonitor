package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import views.html.main;

class BashCaller {
	public static void call(String command) throws IOException {

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		do {
			String s = reader.readLine();
			if (s != null) {
				System.out.println(s);
			} else {
				break;
			}
		} while (true);

	}
	
	public static void main(String[] args) {
		try {
			call("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}