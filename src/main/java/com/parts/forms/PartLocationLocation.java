package com.parts.forms;

import com.db.SessionVars;
import com.forms.EndOfInputException;
import com.forms.FormsArray;
import com.forms.SelectForm;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.parts.security.PartLink;
import com.security.ExceptionCoding;
import com.security.MyLinkObject;

public class PartLocationLocation extends SelectForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2970009344808917363L;

	public PartLocationLocation(SessionVars sVars) throws Exception {
		super(sVars, PartLocationLocation.class.getCanonicalName());
//		if (sVars.fmd.column + 3 > sVars.fmd.get(sVars.fmd.row).size())
//			throw new ExceptionCoding("not enough space in the row for Part, Location, Location");
//		if (sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).obj.getLogicalName()
//				.equals(Part.getSimpleClassNameStatic()))
//			throw new ExceptionCoding("first object not a part");
//		if (sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column + 1).obj.getLogicalName()
//				.equals(Location.getSimpleClassNameStatic()))
//			throw new ExceptionCoding("second object not a location");
//		if (sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column + 2).obj.getLogicalName()
//				.equals(Location.getSimpleClassNameStatic()))
//			throw new ExceptionCoding("third object not a location");

//		partRow = sVars.fmd.row;
//		partColumn = sVars.fmd.column;
//		part = (Part) sVars.fmd.get(partRow).get(partColumn).obj;
//		sourceLocation = (Location) sVars.fmd.get(partRow).get(partColumn + 1).obj;
//		destinationLocation = (Location) sVars.fmd.get(partRow).get(partColumn + 2).obj;
	}

//	int partRow, partColumn = -1;
//	Part part = null;
//	Location sourceLocation, destinationLocation = null;
	public final String MOVEITEMBUTTON = PartLocationLocation.class.getCanonicalName() + "a";
	public final String MOVECONTENTSBUTTON = PartLocationLocation.class.getCanonicalName() + "b";
	public final String MOVETONEWPARENTBUTTON = PartLocationLocation.class.getCanonicalName() + "c";

	@Override
	public FormsArray getForm(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		if (isForMe(sVars)) {
			Parties parties = new Parties(sVars);
//			if (sVars.fmd.direction.equals(FormsMatrixDynamic.PARTNER.PARTNERBOTHSIDES)) {
			if (parties.part.isLoaded() && parties.leftLocation.isLoaded() && parties.rightLocation.isLoaded()
					&& parties.part.linkToChildExists(parties.leftLocation)) {
				ret.submitButton(
						"Move '" + parties.part.getInstanceName() + "' from '" + parties.leftLocation.getInstanceName()
								+ "' to '" + parties.rightLocation.getInstanceName() + "'",
						MOVEITEMBUTTON);
			}
			if (parties.leftLocation.isLoaded() && parties.rightLocation.isLoaded()) {
				ret.submitButton("Move the contents of '" + parties.leftLocation.getInstanceName() + "' to '"
						+ parties.rightLocation.getInstanceName() + "'", MOVECONTENTSBUTTON);
			}
			if (new MyLinkObject(parties.leftLocation, parties.rightLocation, sVars).okToAddChild())
				ret.submitButton("Make '" + parties.leftLocation.getInstanceName() + "' a child of '"
						+ parties.rightLocation.getInstanceName() + "'", MOVETONEWPARENTBUTTON);

		}
		return ret;
	}

	class Parties {
		Part part = null;
		Location leftLocation = null;
		Location rightLocation = null;

		Parties(SessionVars sVars) throws ExceptionCoding {
			if (sVars.fmd.getToMyLeft().obj instanceof Part)
				part = (Part) sVars.fmd.getToMyLeft().obj;
			else
				throw new ExceptionCoding("not a part");

			if (sVars.fmd.getSearchTarget().obj instanceof Location)
				leftLocation = (Location) sVars.fmd.getSearchTarget().obj;
			else
				throw new ExceptionCoding("not a location");

			if (sVars.fmd.getToMyRight().obj instanceof Location)
				rightLocation = (Location) sVars.fmd.getToMyRight().obj;
			else
				throw new ExceptionCoding("not a location");

		}
	}

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
//		if (isForMe(sVars)) {
		Parties parties = new Parties(sVars);

//			ret.addAll(super.extractParams(sVars));
		if (sVars.hasParameterKey(MOVEITEMBUTTON)) {
//				parties.part.reChild(parties.leftLocation, parties.rightLocation);
//				public MyObject reChild(MyObject oldChild, MyObject newChild) throws Exception {
			if (!parties.part.isLoaded())
				throw new Exception("Part not loaded.<br>" + new Exception().getStackTrace()[0]);
			if (!parties.leftLocation.isLoaded())
				throw new Exception("source location not loaded.<br>" + new Exception().getStackTrace()[0]);
			if (!parties.rightLocation.isLoaded())
				throw new Exception("destination location not loaded.<br>" + new Exception().getStackTrace()[0]);
			PartLink mlo = new PartLink(parties.part, parties.leftLocation, sVars);
			mlo.find();
			int quantity = mlo.getLinkQuantity();
			mlo.deleteTest();
			mlo.deleteUnconditionally();
			
			mlo = new PartLink(parties.part, parties.rightLocation, sVars);
			mlo.setLinkQuantity(quantity);
			mlo.add();
			throw new EndOfInputException(ret);
		}

		if (sVars.hasParameterKey(MOVECONTENTSBUTTON)) {
			parties.leftLocation.moveChildrenOfThisParentToNewParent(parties.rightLocation, new Part(sVars));
			throw new EndOfInputException(ret);
		}

		if (sVars.hasParameterKey(MOVETONEWPARENTBUTTON)) {
			parties.leftLocation.moveToNewParentRecursive(parties.rightLocation);
			throw new EndOfInputException(ret);
		}

		return ret;
//		} else {
//			ret.addAll(super.extractParams(sVars));
//			return ret;
//		}
	}

	boolean isForMe(SessionVars sVars) throws ExceptionCoding {
		switch (sVars.fmd.direction) {
		case PARTNERBOTHSIDES:
			if (!(sVars.fmd.getToMyLeft().obj instanceof Part))
				return false;
			if (!(sVars.fmd.getSearchTarget().obj instanceof Location))
				return false;
			if (!(sVars.fmd.getToMyRight().obj instanceof Location))
				return false;
			return true;
		case PARTNERTOTHELEFT:
		case PARTNERTOTHERIGHT:
		case NONE:
		default:
			return false;
		}
	}
}
