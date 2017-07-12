package com.fileprocessor.akka.actor;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fileprocessor.akka.actorHelpers.EndOfFileHelper;
import com.fileprocessor.akka.actorHelpers.LineHelper;
import com.fileprocessor.akka.actorHelpers.StartOfFileHelper;
import com.typesafe.config.ConfigFactory;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestActorRef;
import akka.testkit.TestKit;

public class AggregatorActorTest extends TestKit {

	static ActorSystem _system = ActorSystem.create("TestSys", ConfigFactory.load().getConfig("TestSys"));

	public AggregatorActorTest() {
		super(_system);
	}

	@Test
	public void MytestActor() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());

		Assert.assertNull(AggregatorActor.getProcessedFileName());
		String fileName = "Test.txt";
		aggregatorActor.tell(new StartOfFileHelper(fileName), aggregatorActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 1);
		Assert.assertEquals(AggregatorActor.getProcessedFileName(), fileName);
		expectNoMsg();

		aggregatorActor.tell(new LineHelper(fileName, "Some test data \n some extra data"), aggregatorActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 1);
		Assert.assertEquals(AggregatorActor.getProcessedFileName(), fileName);
		expectNoMsg();
		aggregatorActor.tell(new EndOfFileHelper(fileName), ActorRef.noSender());
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void MytestActorWithException() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());

		aggregatorActor.tell(null, aggregatorActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
		aggregatorActor.tell(1235, aggregatorActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
		aggregatorActor.tell("Some string", aggregatorActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
		aggregatorActor.tell(new LineHelper("Hello", "Hai"), aggregatorActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
		aggregatorActor.tell(new EndOfFileHelper("UnknownFile"), aggregatorActor);
		Assert.assertEquals(AggregatorActor.getMapCount(), 0);
		Assert.assertNull(AggregatorActor.getProcessedFileName());
	}

}
