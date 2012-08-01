package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class HiveUtils {

	
	
	private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
//	private static String hive_url = ConfigUtils.HIVE_URL();
	private static String hive_url = "jdbc:hive://127.0.0.1:10000/default";

	public static boolean buildData(List<LogInfo> list) {
		Connection con = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection(hive_url, "", "");
			Statement stmt = con.createStatement();
			stmt.executeQuery("set hive.exec.dynamic.partition=true");
			stmt.close();
			
			String script = "LOAD DATA LOCAL INPATH '%file%' INTO TABLE apilog_pro PARTITION(time='%time%', api='%api%')";
			
			for(LogInfo info : list){
				stmt = con.createStatement();
				String tmp = script.replaceAll("%file%", info.getFile());
				tmp = tmp.replaceAll("%time%", info.getTime());
				tmp = tmp.replaceAll("%api%", info.getApi());
				stmt.executeQuery(tmp);
				stmt.close();
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
	
	static class LogInfo{
		String file;
		String time;
		String api;
		
		public LogInfo(String file, String time, String api) {
			this.file = file;
			this.time = time;
			this.api = api;
		}
		
		public String getFile() {
			return file;
		}
		public void setFile(String file) {
			this.file = file;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getApi() {
			return api;
		}
		public void setApi(String api) {
			this.api = api;
		}
		
	}

}