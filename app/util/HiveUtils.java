package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HiveUtils {

	private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
//	private static String hive_url = ConfigUtils.HIVE_URL();
	private static String hive_url = "jdbc:hive://127.0.0.1:10000/default";

	public static boolean buildData(String file, String time, String api) {
		Connection con = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection(hive_url, "", "");
			Statement stmt = con.createStatement();

			String script = loadScript("loaddata.sql");
			script = script.replaceAll("%file%", file);
			script = script.replaceAll("%time%", time);
			script = script.replaceAll("%api%", api);

			
			ResultSet res = stmt.executeQuery("set hive.exec.dynamic.partition=true");
			
			stmt = con.createStatement();
			res = stmt.executeQuery(script);
			if (res.next()) {
				System.out.println(res.getString(1));
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {

				}
			}
		}
	}

	private static String loadScript(String fileName) throws IOException {
		String s = "";
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(DirWatcher.class.getResourceAsStream(fileName)));

			String tmp;
			while ((tmp = in.readLine()) != null) {
				s += tmp + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}

		}

		return s;
	}

}