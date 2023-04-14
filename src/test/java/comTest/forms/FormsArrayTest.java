package comTest.forms;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.forms.FormsArray;
import com.security.ExceptionCoding;

public class FormsArrayTest {
SessionVars sVars = null;
	@Before
	public void setUp() throws Exception {
		if (sVars==null)
			sVars = new SessionVars(true);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTextBox() {
		FormsArray ret = new FormsArray();
		try {
			ret.textBox("internal textbox name", 55, "external textbox label", "my textbox value", false, false);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		System.out.println("body: " + ret.body);
	}

	@Test
	public void testSubmitClickWithId() {
		FormsArray ret = new FormsArray();
		ret.submitClickWithId("idToWatch", "myFavoriteKey", "myFavoriteValue");
		System.out.println("body: " + ret.body);
	}

	@Test
	/**
	 * verify that we catch a form that does not have returnTo set
	 */
	public void testReturnTo() {
		FormsArray ret = new FormsArray();		
		// no returnTo set yet. should not be a valid form
		try {
			ret.validInternalForm();
			fail("allowed a form without returnTo set");
		} catch (ExceptionCoding e) {
		}
	}
	
	/**
	 * catch forms that try to set returnTo more than once
	 */
	@Test
	public void testReturnTo2() {
		FormsArray ret = new FormsArray();
		// setting returnTo for the first time should be ok
		try {
			ret.setReturnTo("ferShure");
		} catch (ExceptionCoding e1) {
			fail("could not set returnTo. "+e1.getLocalizedMessage());
		}
		
		// setting returnTo the second time should fail
		try {
			ret.setReturnTo("secondTime");
			fail("allowed to set returnTo twice");
		} catch (ExceptionCoding e1) {
		}
	}
	
	/**
	 * verify that "form action=returnTo" is set correctly
	 */
	@Test
	public void testReturnTo3() {
		FormsArray ret = new FormsArray();
		// setting returnTo for the first time should be ok
		try {
			ret.setReturnTo("ferShure");
		} catch (ExceptionCoding e1) {
			fail("could not set returnTo. "+e1.getLocalizedMessage());
		}
		String results = ret.generateHTML();
		if (!results.contains("<form action=\"ferShure\""))
			fail("did not find <form action=\"ferShure\" in "+results);
	}
}
