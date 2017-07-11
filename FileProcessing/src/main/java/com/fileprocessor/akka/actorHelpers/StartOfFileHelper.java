package com.fileprocessor.akka.actorHelpers;

public final class StartOfFileHelper {
	private String filePath;

	public StartOfFileHelper(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}
}
