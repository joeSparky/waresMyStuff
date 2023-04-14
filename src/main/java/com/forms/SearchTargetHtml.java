package com.forms;

/**
 * store the SearchTarget information in the html so that the SearchTarget can
 * be identified with the HTML stream from the user
 * 
 * @author joe
 *
 */
public class SearchTargetHtml {
	/**
	 * return an HTML string containing the SearchTarget identifier
	 * 
	 * @param searchTarget
	 * @param htmlIdentifier
	 * @return
	 */
	public String toHtml(SearchTarget searchTarget, FormsMatrixDynamic fmd) {
		return "outer_" + fmd.row + "_inner_" + fmd.column + "_" + searchTarget.obj.getCanonicalName() + "_"
				+ fmd.direction.name();
	}

	public SearchTargetHtml() {
	}

	public int row = -1;
	public int column = -1;
	public String canonicalName = null;
	public FormsMatrixDynamic.PARTNER direction = null;
	public String errorString = null;

	/**
	 * return the SearchTarget identified in the HTML string
	 * 
	 * @param sVars
	 * @return
	 * @throws Exception
	 */
	public SearchTargetHtml(String receivedParameter) throws Exception {
		String component[] = receivedParameter.split("_");
		if (component.length != 6)
			throw new Exception("expected 6 fields in " + receivedParameter + ", got " + component.length);
		if (!component[0].equals("outer"))
			throw new Exception("don't find 'outer' in " + component[0] + " in " + receivedParameter);
		if (!component[2].equals("inner"))
			throw new Exception("didn't find 'inner' in " + component[2] + " in " + receivedParameter);
		row = Integer.parseInt(component[1]);
		column = Integer.parseInt(component[3]);
		canonicalName = component[4];
		direction = FormsMatrixDynamic.PARTNER.valueOf(component[5]);
	}

	public boolean sanityFails(FormsMatrixDynamic fmd) {
		if (!fmd.get(row).get(column).obj.getCanonicalName().equals(canonicalName)) {
			errorString = "expected " + fmd.get(row).get(column).obj.getCanonicalName() + ", got " + canonicalName;
			return true;
		}
		if (row > fmd.size() - 1) {
			errorString = "have " + fmd.size() + " rows, received hiddenField row of " + row;
			return true;
		}
		if (column > fmd.get(row).size()) {
			errorString = "have " + fmd.get(row).size() + " columns in row " + row + ", received hiddenField column of "
					+ column;
			return true;
		}
		return false;
	}
}
