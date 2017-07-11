package com.fileprocessor.akka;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
public class ApplicationTest {

	Application app = null;

	@BeforeTest
	public void before() {
		app = new Application();
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testAppIllegalStart() {
		app.start();
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testAppIllegalStop() {
		app.shutdown();
	}

	@Test
	public void testAppLegal() {
		File file = new File(System.getProperty("user.dir")+"/TestData.txt");
		if (file.exists()) {
			Application.FILE_PATH = file.getPath();
			app.init();
			app.start();
			app.shutdown();
		} else {
			Assert.fail("Not able to load the input file");
		}
	}

}
