package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.apache.commons.io.FilenameUtils;

import util.HiveUtils.LogInfo;

public class DirWatcher{

	int watchID;
//	String rawDataPath = ConfigUtils.RAWDATA_DIR_PATH();
	String rawDataPath = "/mnt/apilogs/rawdata";
	
	private static final int SIZE = 50; //batch
	private static final int TIMES_TO_SLEEP = 5; // number of batch
	private static final long SLEEP_TIME = 20 * 60 * 1000; // 20'
	
	private AtomicInteger count = new AtomicInteger(0);
	private List<LogInfo> list;
	
	public void start() throws IOException {
		
		if(rawDataPath.endsWith("/")){
			rawDataPath = rawDataPath.substring(0, rawDataPath.length() - 1);
		}
		
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

		//file name must be in format: yyyymmdd/filename
		public void fileCreated(int wd, String rootPath, String name) {
			String rawDataPath = rootPath + "/" + name;
			File file = new File(rawDataPath);
			
			if(file.isFile()){
				System.out.println("Process file : " + rawDataPath);
				
				String tmp[] = name.split("/");
				if(tmp.length != 2){
					return;
				}
				
				String time = tmp[0];
				String api = FilenameUtils.removeExtension(tmp[1]);
				
				if(list == null){
					list = new ArrayList<LogInfo>();
				}

				list.add(new LogInfo(file.getPath(), time, api));
				
				if(list.size() >= SIZE ){
					if(HiveUtils.buildData(list)){
						System.out.println("=================LOAD DATA TO APILOG SUCCESS, size: " + SIZE);
					}else{
						System.out.println("LOAD DATA TO APILOG FAILED!!!");
					}
					list = new ArrayList<LogInfo>();
					System.out.println( "======" + count.incrementAndGet());
				}
				
				//after run 300 file -> sleep 5 second
				if(count.get() > TIMES_TO_SLEEP ){
					System.out.println("==TIME TO SLEEP==");
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						
					}
					count = new AtomicInteger(0);
				}
				
			}else{
				System.out.println("Only process file");
			}
		}

	}

	public static void main(String[] args) throws Exception{
		DirWatcher w = new DirWatcher();
		w.start();
		Thread.sleep(100000000);
	}

}

