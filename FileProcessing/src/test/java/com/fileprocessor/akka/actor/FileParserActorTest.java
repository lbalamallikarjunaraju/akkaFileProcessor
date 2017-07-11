package com.fileprocessor.akka.actor;

import org.testng.annotations.Test;

import com.typesafe.config.ConfigFactory;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.testkit.TestActorRef;
import akka.testkit.TestKit;

public class FileParserActorTest extends TestKit {

	static ActorSystem _system = ActorSystem.create("TestSys", ConfigFactory.load().getConfig("TestSys"));

	public FileParserActorTest() {
		super(_system);
	}

	@Test
	public void MytestActor() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());
		final TestActorRef<Actor> fileParserActor = TestActorRef.create(system(),
				FileParserActor.props(aggregatorActor));
		
		fileParserActor.tell(System.getProperty("user.dir") + "/TestData.txt", fileParserActor);
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void MytestActorWithException() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());
		final TestActorRef<Actor> fileParserActor = TestActorRef.create(system(),
				FileParserActor.props(aggregatorActor));
		
		
		fileParserActor.tell(null, aggregatorActor);
		fileParserActor.tell(1235, aggregatorActor);
		fileParserActor.tell("nodata", aggregatorActor);
	}

}
