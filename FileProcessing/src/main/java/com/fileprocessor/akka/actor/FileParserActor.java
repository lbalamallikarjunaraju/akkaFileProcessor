package com.fileprocessor.akka.actor;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fileprocessor.akka.actorHelpers.EndOfFileHelper;
import com.fileprocessor.akka.actorHelpers.LineHelper;
import com.fileprocessor.akka.actorHelpers.SimpleSuccessIndicator;
import com.fileprocessor.akka.actorHelpers.StartOfFileHelper;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class FileParserActor extends UntypedAbstractActor {

	private ActorRef aggregatorActor;
	LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

	public FileParserActor(ActorRef aggregatorActor) {
		this.aggregatorActor = aggregatorActor;
		logger.info("File Parser Actor initialized");
	}

	public static Props props(ActorRef aggregatorActor) {
		return Props.create(FileParserActor.class, aggregatorActor);
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message == null)
			throw new RuntimeException("Message should not be null");

		if (message instanceof String) {
			final String path = (String) message;

			if (Files.isRegularFile(Paths.get(path))) {
				aggregatorActor.tell(new StartOfFileHelper(path), getSelf());
				Files.lines(Paths.get(path), Charset.defaultCharset())
						.forEachOrdered(line -> aggregatorActor.tell(new LineHelper(path, line), getSelf()));
				aggregatorActor.tell(new EndOfFileHelper(path), getSelf());
			} else {
				throw new RuntimeException("Expected format is file only");
			}
		} else if (message instanceof SimpleSuccessIndicator) {
			getContext().system().actorOf(FileScannerActor.props(null)).tell(new SimpleSuccessIndicator(), null);
		} else {
			throw new RuntimeException("Unknown message");
		}
	}

}
