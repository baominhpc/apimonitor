package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

public class DirWatcher{

	int watchID;
//	String rawDataPath = ConfigUtils.RAWDATA_DIR_PATH();
//	String dataPath = ConfigUtils.DATA_DIR_PATH();
	
	String rawDataPath = "/mnt/apilogs/rawdata";
	String dataPath = "/mnt/apilogs/data";
	
	String editDataScript = "";
	public void start() throws IOException {
		
		if(rawDataPath.endsWith("/")){
			rawDataPath = rawDataPath.substring(0, rawDataPath.length() - 1);
		}
		
		if(dataPath.endsWith("/")){
			dataPath = dataPath.substring(0, dataPath.length() - 1);
		}
		
		editDataScript = getCommand("script.sh");
		
		System.out.println(editDataScript);
		
		int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;

		boolean watchSubtree = true;

		JNotify.addWatch(rawDataPath, mask, watchSubtree, new Listener());

		System.out.println("----------STARTED WATCHING DIR : " + rawDataPath + " ----------");

		
	}
	
	public void stop() throws JNotifyException {
		boolean res = JNotify.removeWatch(watchID);
		if(res){
			System.out.println("----------STOPPED WATCHING DIR : " + rawDataPath + " ----------");
		}else{
			System.out.println("----------STOP WATCHING DIR : " + rawDataPath + " FAILED ----------");
		}
		
	}

	class Listener implements JNotifyListener {
		public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
//			print("renamed " + rootPath + " : " + oldName + " -> " + newName + " - wd: " + wd) ;
		}

		public void fileModified(int wd, String rootPath, String name) {
//			if(!name.endsWith("/")){
//				print("modified " + rootPath + " : " + name + " : " + wd);
//			}
		}

		public void fileDeleted(int wd, String rootPath, String name) {
//			print("deleted " + rootPath + " : " + name + " : " + wd);
		}

		public void fileCreated(int wd, String rootPath, String name) {
			String rawDataPath = rootPath + "/" + name;
			File file = new File(rawDataPath);
			
			if(file.isFile()){
				System.out.println("Process file : " + rawDataPath);
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					
				}
				String time = getTimeFromFile(name);
				try {
					//format data
					String dataRootPath = dataPath + "/" + time;
					String dataFile = dataPath + "/" + name;
					
					File timeDir = new File(dataRootPath);
					if(!timeDir.exists()){
						timeDir.mkdir();
					}
					
					modifyData(rawDataPath, dataFile);
					
					//load data to tmp table
					if(HiveUtils.loadDataToTmp(dataFile)){
						System.out.println("LOAD DATA TO APILOG_TMP SUCCESS!!!");
						if(HiveUtils.buildData(time)){
							System.out.println("LOAD DATA TO APILOG SUCCESS!!!");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				System.out.println("Only process file");
			}
		}

		void print(String msg) {
//			System.err.println(msg);
		}
	}
	
	private void modifyData(String infile, String outfile) throws IOException{
		String command = editDataScript.replaceAll("%script%", "/mnt/apilogs/script/modifydata.sh")
				.replaceAll("%infile%", infile)
				.replaceAll("%outfile%", outfile);
		
		System.out.println("==================");
		System.out.println(command);
		BashCaller.call(command);
		System.out.println("==================");
	}
	
	private String getCommand(String fileName) throws IOException{
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
			if(in != null){
				in.close();
			}
			
		}
		
		return s;
	}
	
	private static String getTimeFromFile(String fullPath){
		if(fullPath.endsWith("/")){
			fullPath = fullPath.substring(0, fullPath.length() - 1);
		}
		int lastIndexOfSlash = fullPath.lastIndexOf("/");
		return fullPath.substring(lastIndexOfSlash - 8, lastIndexOfSlash);
	}
	
	public static void main(String[] args) throws Exception{
		DirWatcher w = new DirWatcher();
		w.start();
		Thread.sleep(100000000);
	}

}
