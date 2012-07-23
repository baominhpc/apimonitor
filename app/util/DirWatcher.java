package util;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyListener;

public class DirWatcher {

	public static void main(String[] args) throws Exception {
		start();
	}
	
	public static void start() throws Exception {
		String path = "/home/giangvo";

		int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED
				| JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;

		boolean watchSubtree = false;

		int watchID = JNotify.addWatch(path, mask, watchSubtree, new Listener());


//		boolean res = JNotify.removeWatch(watchID);
		
	}

	static class Listener implements JNotifyListener {
		public void fileRenamed(int wd, String rootPath, String oldName,
				String newName) {
			print("renamed " + rootPath + " : " + oldName + " -> " + newName);
		}

		public void fileModified(int wd, String rootPath, String name) {
			print("modified " + rootPath + " : " + name);
		}

		public void fileDeleted(int wd, String rootPath, String name) {
			print("deleted " + rootPath + " : " + name);
		}

		public void fileCreated(int wd, String rootPath, String name) {
			print("created " + rootPath + " : " + name);
		}

		void print(String msg) {
			System.err.println(msg);
		}
	}

}
