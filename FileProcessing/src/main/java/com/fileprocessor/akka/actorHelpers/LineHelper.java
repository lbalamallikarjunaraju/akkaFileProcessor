package com.fileprocessor.akka.actorHelpers;

public final class LineHelper {

	private String fileName;
	private String line;

	public LineHelper(String fileName, String line) {
		this.fileName = fileName;
		this.line = line;
	}

	public String getFileName() {
		return fileName;
	}

	public String getLine() {
		return line;
	}

}
