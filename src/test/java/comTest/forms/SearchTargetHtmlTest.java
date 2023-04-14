package comTest.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.forms.FormsMatrixDynamic;
import com.forms.SearchTarget;
import com.forms.SearchTargetHtml;
import com.forms.SearchTargets;
import com.parts.inOut.Part;

public class SearchTargetHtmlTest {

	SearchTarget searchTarget = null;
	SessionVars sVars = null;
	Part part = null;
	FormsMatrixDynamic fmd = null;
	String expect = null;

	@Before
	public void setUp() {
		try {
			sVars = new SessionVars(true);
			part = new Part(sVars);
			searchTarget = new SearchTarget(part, sVars);
			searchTarget.row = 29;
			searchTarget.column = 5;
			SearchTargets searchTargets = new SearchTargets(sVars);
			fmd = new FormsMatrixDynamic(sVars);

			// create a row of three parts
			for (int i = 0; i < 3; i++)
				searchTargets.add(searchTarget);
			fmd.add(searchTargets);

			// row of 5 parts
			for (int i = 0; i < 5; i++)
				searchTargets.add(searchTarget);
			fmd.add(searchTargets);

			// row of 2 parts
			for (int i = 0; i < 2; i++)
				searchTargets.add(searchTarget);
			fmd.add(searchTargets);

			// set the focus on the 4th part in the second row
			fmd.row = 1;
			fmd.column = 3;

			fmd.direction = FormsMatrixDynamic.PARTNER.PARTNERTOTHELEFT;
			
			expect = "outer_1_inner_3_" + searchTarget.obj.getCanonicalName() + "_"
					+ FormsMatrixDynamic.PARTNER.PARTNERTOTHELEFT.toString();

		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void toHtmlTest() {
//		String expect = "outer_1_inner_3_" + searchTarget.obj.getCanonicalName() + "_"
//				+ FormsMatrixDynamic.DIRECTION.NEIGHBOR_TO_THE_LEFT.toString();
		if (!new SearchTargetHtml().toHtml(searchTarget, fmd).equals(expect))
			fail("expected " + expect + ", got " + new SearchTargetHtml().toHtml(searchTarget, fmd));

	}

	@Test
	public void fromHtmlTest() {

		SearchTargetHtml result = null;
//		String expect = "outer_1_inner_3_" + searchTarget.obj.getCanonicalName() + "_"
//				+ FormsMatrixDynamic.DIRECTION.NEIGHBOR_TO_THE_LEFT.toString();
		try {
			result = new SearchTargetHtml(expect);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (result.row != 1)
			fail("expected row=1, got row=" + result.row);
		if (result.column != 3)
			fail("expected column=3, got column=" + result.column);
		if (!result.canonicalName.equals(Part.class.getCanonicalName()))
			fail("expected favoriteCanonicalName, got " + result.canonicalName);
	}

	@Test
	public void notEnoughUnderscoresTest() {
		try {
			new SearchTargetHtml("not_enoughUnderscores");
			fail("did not throw an exception on incorrect format");
		} catch (Exception e) {

		}
	}

	@Test
	public void noIntegerAfterFirstUnderscoreTest() {
		try {
			new SearchTargetHtml("outer_notAnInteger_inner_5");
			fail("did not throw an exception on non-integer after first underscore");
		} catch (Exception e) {

		}
	}

	@Test
	public void noIntegerAfterThirdUnderscoreTest() {
		try {
			new SearchTargetHtml("outer_2_inner_notAnInteger");
			fail("did not throw an exception on non-integer after third underscore");
		} catch (Exception e) {

		}
	}

}
