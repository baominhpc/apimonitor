package service.impl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestHive {

	private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";

	public static void main(String[] args) throws Exception {
		loadData();
		
	}
	
	private static void createTable() throws Exception{
		String script = loadScript("/home/gnt/schema.txt");
		script = script.substring(0, script.length() - 1);
		Class.forName(driverName);
		Connection con = DriverManager.getConnection(
				"jdbc:hive://localhost:10000/default", "", "");
		Statement stmt = con.createStatement();
		
		ResultSet res = stmt.executeQuery(script);

		if (res.next()) {
			System.out.println(res.getString(1));
		}

	}
	
	private static void loadData() throws Exception{
		String script = loadScript("/home/gnt/loaddata.txt");
		script = script.substring(0, script.length() - 1);
		
		script = script.replaceAll("%filename%", "/home/gnt/log.txt");
		script = script.replaceAll("%tablename%", "apilog");
		
		Class.forName(driverName);
		Connection con = DriverManager.getConnection(
				"jdbc:hive://localhost:10000/default", "", "");
		Statement stmt = con.createStatement();
		
		ResultSet res = stmt.executeQuery(script);

		if (res.next()) {
			System.out.println(res.getString(1));
		}

	}
	
	private static String loadScript(String scriptName) throws Exception{
		String script = "";
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(scriptName));
			String str;
			while ((str = in.readLine()) != null) {
				script += str + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
		
		return script;
	}
}