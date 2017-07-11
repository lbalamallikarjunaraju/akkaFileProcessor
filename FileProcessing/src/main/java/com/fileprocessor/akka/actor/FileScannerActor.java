package com.fileprocessor.akka.actor;

import java.io.File;
import java.util.Arrays;

import com.fileprocessor.akka.actorHelpers.SimpleSuccessIndicator;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class FileScannerActor extends UntypedAbstractActor {

	private ActorRef fileParserActor;
	LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

	public FileScannerActor(ActorRef fileParserActor) {
		this.fileParserActor = fileParserActor;
	}

	private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("10 second"),
			DeciderBuilder.match(RuntimeException.class, ex -> SupervisorStrategy.resume()).build());

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message == null)
			throw new RuntimeException("Message should not be null");
		if (message instanceof String) {
			File file = new File((String) message);
			if (file.isDirectory()) {
				logger.info("Directory Found ::: resending the request to parse: " + file.getPath());
				if (file.listFiles() != null) {
					logger.info("Number of files/Dirs inside the directory " + file.listFiles().length);
					final ActorRef tempSender = getContext().sender();
					Arrays.asList(file.listFiles()).parallelStream()
							.forEach(newFile -> tempSender.tell(newFile.getPath(), getSelf()));
				}
			} else if (file.isFile()) {
				logger.info("Processing the file ::: " + message);
				fileParserActor.tell(message, getSelf());
			} else {
				throw new RuntimeException("The path is neither a dir nor a file");
			}
		} else if (message instanceof SimpleSuccessIndicator) {
			logger.info("Got success message");
		} else {
			throw new RuntimeException("Unknown message");
		}
	}

	public static Props props(ActorRef fileParserActor) {
		return Props.create(FileScannerActor.class, fileParserActor);
	}

}
