package com.fileprocessor.akka.actor;

import org.testng.annotations.Test;

import com.typesafe.config.ConfigFactory;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.testkit.TestActorRef;
import akka.testkit.TestKit;

public class FileScannerActorTest extends TestKit {

	static ActorSystem _system = ActorSystem.create("TestSys", ConfigFactory.load().getConfig("TestSys"));

	public FileScannerActorTest() {
		super(_system);
	}

	@Test
	public void MytestActor() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());
		final TestActorRef<Actor> fileParserActor = TestActorRef.create(system(),
				FileParserActor.props(aggregatorActor));
		final TestActorRef<Actor> fileScannerActor = TestActorRef.create(system(),
				FileScannerActor.props(fileParserActor));
		fileScannerActor.tell(System.getProperty("user.dir") + "/TestData.txt", fileScannerActor);
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void MytestActorWithException() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());
		final TestActorRef<Actor> fileParserActor = TestActorRef.create(system(),
				FileParserActor.props(aggregatorActor));
		final TestActorRef<Actor> fileScannerActor = TestActorRef.create(system(),
				FileScannerActor.props(fileParserActor));
		
		fileScannerActor.tell(null, aggregatorActor);
		fileScannerActor.tell(1235, aggregatorActor);
		fileScannerActor.tell("nodata", aggregatorActor);
	}

}
