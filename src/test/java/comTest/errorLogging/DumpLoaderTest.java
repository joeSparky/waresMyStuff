package comTest.errorLogging;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.errorLogging.DumpLoader;

import comTest.utilities.Utilities;

public class DumpLoaderTest {

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	@Test
	public void testDumpLoader() {
		DumpLoader.dumpLoader();
	}

}
