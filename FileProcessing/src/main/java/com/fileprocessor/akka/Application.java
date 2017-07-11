package com.fileprocessor.akka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fileprocessor.akka.actor.AggregatorActor;
import com.fileprocessor.akka.actor.FileParserActor;
import com.fileprocessor.akka.actor.FileScannerActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.LoggingAdapter;

public class Application {
	public static String FILE_PATH;
	private ActorSystem _system = null;
	private LoggingAdapter logger = null;

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter directory or file path: ");
		FILE_PATH = reader.readLine();
		if (FILE_PATH.trim().length() == 0) {
			throw new RuntimeException("Path should not be empty");
		}
		Application app = new Application();
		app.init();
		app.start();
		app.shutdown();
	}

	public final void init() {
		_system = ActorSystem.create("fileProcessor");
		logger = _system.log();
		logger.info("Initialized the Actor System");

	}

	public final void start() {
		if (_system != null) {
			ActorRef aggregatorActor = _system.actorOf(AggregatorActor.props());
			ActorRef fileParserActor = _system.actorOf(FileParserActor.props(aggregatorActor));
			ActorRef fileScannerActor = _system.actorOf(FileScannerActor.props(fileParserActor));

			fileScannerActor.tell(FILE_PATH, fileScannerActor);
		} else {
			throw new RuntimeException("Illeagal start of actor system");
		}
	}

	public final void shutdown() {
		if (_system != null) {
			if (AggregatorActor.getMapCount() == 0) {
				try {
					Thread.sleep(Thread.currentThread().getThreadGroup().activeCount() * 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// I know this is weird.
				// Need to find out a better way to shutdown properly.
				if (AggregatorActor.getMapCount() == 0) {
					_system.terminate();
				}
			}
		} else {
			throw new RuntimeException("Illeagal access to shutdown actor system");
		}
	}
}
