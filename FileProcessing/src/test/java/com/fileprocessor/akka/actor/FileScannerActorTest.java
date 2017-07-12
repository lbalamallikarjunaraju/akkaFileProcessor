package com.fileprocessor.akka.actor;

import org.testng.Assert;
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

		String fileName = System.getProperty("user.dir") + "/TestData.txt";
		fileScannerActor.tell(fileName, fileScannerActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		Assert.assertEquals(AggregatorActor.getProcessedFileName(), fileName);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void MytestActorWithException() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());
		final TestActorRef<Actor> fileParserActor = TestActorRef.create(system(),
				FileParserActor.props(aggregatorActor));
		final TestActorRef<Actor> fileScannerActor = TestActorRef.create(system(),
				FileScannerActor.props(fileParserActor));

		fileScannerActor.tell(null, aggregatorActor);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		fileScannerActor.tell(1235, aggregatorActor);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		fileScannerActor.tell("nodata", aggregatorActor);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
	}

}
