package com.forms;

import com.db.SessionVars;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.parts.security.PartLink;
import com.security.User;

public class MainPartsForm extends SmartForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5227105245588033139L;

	/**
	 * top of forms heap
	 */
	public MainPartsForm(SessionVars sVars) throws Exception {
		super(sVars, MainPartsForm.class.getCanonicalName());
//		if (sVars.fmd == null)
		sVars.fmd = new FormsMatrixDynamic(sVars);
		// create a partLink table so that part->location link is created correctly
		new PartLink(new Part(sVars), new Location(sVars), sVars);

		// get the user information out of the session
		User user = new User(sVars);
//		Internals.dumpStringContinue("new User(sVars)");

		user.find(sVars.getUserNumber());
		Part part = new Part(sVars);
		Location location = new Location(sVars);
		// moving stuff between locations
		Location destinationLocation = new Location(sVars);

		/////////// main row
		SearchTargets objs = new SearchTargets(sVars);
		// force an update of the when selectAndEditForm does a getForm
		objs.add(part);
		// need editAndSelect to create an item time stamp
//		objs.add(item, role.getEditSelectType(user, item));
		objs.add(location);
		objs.add(destinationLocation);
		sVars.fmd.add(objs);

		// give the fmd to SelectSAndEditForm
		sVars.se = new SelectAndEditForm(sVars, sVars.fmd);
	}

	@Override
	public FormsArray getForm(SessionVars sVars) throws Exception {
		return sVars.se.getForm(sVars);
	}

	/**
	 * for testing
	 * 
	 * @return
	 */
//	public FormsMatrixDynamic getFormsMatrixDynamic() {
//		return fmd;
//	}

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		return sVars.se.extractParams(sVars);
	}

}
