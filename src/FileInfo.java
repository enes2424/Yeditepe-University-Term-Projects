public class FileInfo {
	String filename;
	long bytes;
	String hash;

	FileInfo(String filename, long bytes, String hash) {
		this.filename = filename;
		this.bytes = bytes;
		this.hash = hash;
	}
}
