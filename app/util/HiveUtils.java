package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HiveUtils {

	
	
	private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
//	private static String hive_url = ConfigUtils.HIVE_URL();
	private static String hive_url = "jdbc:hive://192.168.30.168:10000/default";

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
	
	public static boolean buildapiReport(String time) {
		Connection con = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection(hive_url, "", "");
			Statement stmt = con.createStatement();
			
			String sql = "from apilog_pro insert into table api_report PARTITION(time='%time%')select api, count(*), max(elapsetime), min(elapsetime),avg(elapsetime) where time='%time%' group by api";
			sql = sql.replaceAll("%time%", time);
			
			stmt = con.createStatement();
			stmt.executeQuery(sql);
			
			
			
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
	
	public static List<APIReport> getapiReport(String time) {
		List<APIReport> list = new ArrayList<APIReport>();
		Connection con = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection(hive_url, "", "");
			Statement stmt = con.createStatement();
			
			String sql = "select * from api_report where time = '" + time + "'";
			stmt = con.createStatement();
			ResultSet ret = stmt.executeQuery(sql);
			while(ret.next()){
				String api = ret.getString(1);
				int count = ret.getInt(2);
				int max = ret.getInt(3);
				int min = ret.getInt(4);
				int avg = ret.getInt(5);
				list.add(new APIReport(api, count, max, min, avg));
			}
			
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();

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
	
	public static void main(String[] args) {
		buildapiReport("20120729");
	}

}