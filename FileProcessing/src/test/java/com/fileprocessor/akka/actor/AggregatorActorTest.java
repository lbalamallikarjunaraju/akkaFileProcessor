package com.fileprocessor.akka.actor;

import org.testng.annotations.Test;

import com.fileprocessor.akka.actorHelpers.EndOfFileHelper;
import com.fileprocessor.akka.actorHelpers.LineHelper;
import com.fileprocessor.akka.actorHelpers.StartOfFileHelper;
import com.typesafe.config.ConfigFactory;

import akka.actor.Actor;
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
		aggregatorActor.tell(new StartOfFileHelper("Test"), aggregatorActor);
		aggregatorActor.tell(new LineHelper("Test", "Some test data \n some extra data"), aggregatorActor);
		aggregatorActor.tell(new EndOfFileHelper("Test"), aggregatorActor);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void MytestActorWithException() throws InterruptedException {
		final TestActorRef<Actor> aggregatorActor = TestActorRef.create(system(), AggregatorActor.props());

		aggregatorActor.tell(null, aggregatorActor);
		aggregatorActor.tell(1235, aggregatorActor);
		aggregatorActor.tell("Some string", aggregatorActor);
		aggregatorActor.tell(new LineHelper("Hello", "Hai"), aggregatorActor);
		aggregatorActor.tell(new EndOfFileHelper("UnknownFile"), aggregatorActor);
	}

}
