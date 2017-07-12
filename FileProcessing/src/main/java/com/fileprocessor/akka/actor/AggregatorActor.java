package com.fileprocessor.akka.actor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fileprocessor.akka.actorHelpers.EndOfFileHelper;
import com.fileprocessor.akka.actorHelpers.LineHelper;
import com.fileprocessor.akka.actorHelpers.SimpleSuccessIndicator;
import com.fileprocessor.akka.actorHelpers.StartOfFileHelper;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class AggregatorActor extends UntypedAbstractActor {
	private Map<String, Integer> fileMap = new ConcurrentHashMap<>();
	LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
	private static int mapCount;
	private static String fileName = null;

	public static Props props() {
		return Props.create(AggregatorActor.class);
	}

	@Override
	public void onReceive(Object object) throws Throwable {
		if (object instanceof StartOfFileHelper) {
			StartOfFileHelper helper = (StartOfFileHelper) object;
			logger.info("Start Activity with: " + helper.getFilePath());
			fileName = helper.getFilePath();
			Integer tmp = fileMap.get(fileName);
			if (tmp != null) {
				throw new RuntimeException("Un-authorized usage of Start Helper Activity");
			}
			fileMap.put(helper.getFilePath(), 0);
			mapCount = fileMap.size();
		} else if (object instanceof LineHelper) {
			LineHelper lineHelper = (LineHelper) object;
			logger.debug("Line Activity with: " + lineHelper.getFileName());
			Integer counter = fileMap.get(lineHelper.getFileName());
			if (counter != null) {
				String line = lineHelper.getLine();
				int temp = 0;
				if (line != null)
					temp = line.trim().split(" ").length;

				fileMap.put(lineHelper.getFileName(), counter + temp);
				mapCount = fileMap.size();
			} else {
				throw new RuntimeException("Un-authorized read of file");
			}

		} else if (object instanceof EndOfFileHelper) {
			EndOfFileHelper helper = (EndOfFileHelper) object;
			logger.info("End Activity with: " + helper.getFilePath());
			String key = helper.getFilePath();
			Integer totalWordCount = fileMap.get(key);
			if (totalWordCount == null) {
				throw new RuntimeException("Un-authorized use of END activity");
			}
			logger.info("Word count for file: " + key + " is : " + totalWordCount);
			fileMap.remove(key);
			mapCount = fileMap.size();
			// Processed successfully
			sender().tell(new SimpleSuccessIndicator(), getSelf());
		} else {
			throw new RuntimeException("Un-authorized read of file");
		}
	}

	public static int getMapCount() {
		return mapCount;
	}

	public static String getProcessedFileName() {
		return fileName;
	}

}
