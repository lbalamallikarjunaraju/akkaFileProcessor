package com.fileprocessor.akka.actorHelpers;

public final class EndOfFileHelper {
	private String filePath;

	public EndOfFileHelper(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}
}
