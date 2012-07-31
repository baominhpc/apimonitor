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

	public static boolean buildData(String time) {
		Connection con = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection("jdbc:hive://localhost:10000/default", "", "");
			Statement stmt = con.createStatement();

			String script = loadScript("builddata.sql");
			script = script.replaceAll("%time_value%", time);

			
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

	public static boolean loadDataToTmp(String file) {
		Connection con = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection("jdbc:hive://localhost:10000/default", "", "");
			Statement stmt = con.createStatement();

			String script = loadScript("loaddata2tmp.sql");
			script = script.replaceAll("%filename%", file);

			ResultSet res = stmt.executeQuery(script);
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