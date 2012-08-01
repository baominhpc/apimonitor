package util;

public class APIReport{
	String api;
	int count;
	int max;
	int min;
	int avg;
	
	public APIReport(String api, int count, int max, int min, int avg) {
		this.api = api;
		this.count = count;
		this.max = max;
		this.min = min;
		this.avg = avg;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getAvg() {
		return avg;
	}
	public void setAvg(int avg) {
		this.avg = avg;
	}
}