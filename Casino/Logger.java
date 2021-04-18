package Casino;

public class Logger {
	private boolean verbose;
	public Logger(boolean verbose){
		this.verbose = verbose;
	}
	
	public void log(String log) {
		if(this.verbose) {
			System.out.println(log);
		}
	}
	
	public void logFormat(String format) {
		if(this.verbose) {
			System.out.printf(format);
		}
	}
}
