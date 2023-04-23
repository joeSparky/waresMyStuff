package com.forms;

import java.util.HashMap;

import com.db.SessionVars;
import com.security.MyObject;
import com.security.MyObjectsArray;

public class SelectForm extends SmartForm {
	/**
		 * 
		 */
	private static final long serialVersionUID = -3509826242789331244L;

	public SelectForm(SessionVars sVars, String uniqueName) throws Exception {
		super(sVars, uniqueName);
	}

	static final String CANCELBUTTON = SelectForm.class.getCanonicalName() + "a";
	static final String DELETEBUTTON = SelectForm.class.getCanonicalName() + "b";
	static final String DELETETESTBUTTON = SelectForm.class.getCanonicalName() + "c";
	static String DESCENDANTBOX = SelectForm.class.getCanonicalName() + "d";
//	static String INVENTORYBUTTON = SelectForm.class.getCanonicalName() + "e";
	static String PEERBOX = SelectForm.class.getCanonicalName() + "f";
	static String PARENTBUTTON = SelectForm.class.getCanonicalName() + "g";

	class MyVars extends com.forms.MyVars {
		HashMap<String, Integer> childToId = new HashMap<String, Integer>();
		MyObject peer = null;
		MyObject descendant = null;
		boolean survived = true;

		protected MyVars(SessionVars sVars, String uniqueName) throws Exception {
			super(sVars, uniqueName);
			if (get() == null) {
				survived = false;
				put();
			}
		}
	}

	/**
	 * manipulations on a loaded object
	 */
	@Override
	public FormsArray getForm(SessionVars sVars, String uniqueName) throws Exception {
		FormsArray ret = new FormsArray();
		MyVars myVars = (MyVars) new MyVars(sVars, uniqueName).get();
		MyObject currentObject = sVars.fmd.getObject();
		if (!currentObject.isLoaded())
			return ret;
		ret.addAll(getFormSpare1());
		if (currentObject.isRecursive()) {
			ret.addAll(parentButton(sVars, myVars));
//			ret.addAll(addKidsButtons(sVars, myVars));
			ret.addAll(addDescendant(sVars, myVars));
			ret.addAll(addPeer(sVars, myVars));
			if (currentObject.isOrphan(currentObject)) {
				ret.rawText(currentObject.getInstanceName() + " is an orphan or is at the top of its members");
				ret.newLine();
			}
		}
//		if (currentObject.hasInventoryField()) {
//			ret.addAll(addInventoryButton(sVars, myVars));
//		}

		// dump the path to the top using the level above target as a parent type
		MyObjectsArray pathToTheTop = new MyObjectsArray();
		pathToTheTop.getSingleParentsToTop(sVars.fmd, sVars);
		for (int i = pathToTheTop.size() - 1; i >= 0; i--) {
			ret.rawText(pathToTheTop.get(i).getLogicalName() + "::" + pathToTheTop.get(i).getInstanceName());
			ret.newLine();
		}
		ret.rawText(currentObject.getLogicalName() + ":" + currentObject.getInstanceName());
		ret.newLine();
		ret.newLine();
		ret.addAll(deleteCancelButtons(currentObject));
		return ret;
	}

	protected FormsArray deleteCancelButtons(MyObject target) {
		FormsArray ret = new FormsArray();
		ret.submitButton("Cancel " + target.getInstanceName(), CANCELBUTTON);
		ret.submitButton("Test Delete " + target.getInstanceName(), DELETETESTBUTTON);
		ret.submitButton("Delete (No Undo) " + target.getInstanceName(), DELETEBUTTON);
		return ret;
	}

	protected FormsArray getFormSpare1() throws Exception {
		return new FormsArray();
	}

	@Override
	public FormsArray extractParams(SessionVars sVars, String uniqueName) throws Exception {
		FormsArray ret = new FormsArray();
		MyVars myVars = (MyVars) new MyVars(sVars, uniqueName).get();
		MyObject target = sVars.fmd.getObject();
		if (sVars.hasParameterKey(CANCELBUTTON)) {
			sVars.fmd.getObject().clear();
			throw new EndOfInputRedoQueries(ret);
		}
		if (sVars.hasParameterKey(DELETETESTBUTTON)) {
			target.deleteTest();
			ret.errorToUser("No problems with deleting " + target.getInstanceName());
			throw new EndOfInputException(ret);
		}
		if (sVars.hasParameterKey(DELETEBUTTON)) {
			try {
				ret.errorToUser(target.getInstanceName());
				target.deleteUnconditionally();
				ret.errorToUser(" was deleted.");
//				sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).clear();
//				objs.clearChildren(index);
			} catch (Exception e) {
				ret.errorToUser(" was not deleted.");
				ret.errorToUser(e);
			}
			throw new EndOfInputRedoQueries(ret);
		}
//		if (sVars.hasParameterKey(INVENTORYBUTTON)) {
//			if (!target.isOrphan(target)) {
//				ret.addAll(target.markAsInventoried());
////				ret.addAll(updateTheCaller(sVars, target));
//			} else {
//				ret.errorToUser(target.getInstanceName() + " is an orphan or is at the top of its members");
//			}
//			throw new EndOfInputException(ret);
//		}
//		Anchor anchor = myVars.fmd.getAnchor();
//		ret.addAll(myVars.fmd.extractEditAddFormParams(sVars));
//		ret.addAll(super.extractParams(sVars, uniqueName));
		ret.addAll(extractSpare(sVars));
//		ret.addAll(boundaryForm.extractHandleLinkBetween(sVars, callBackVar, this, this, fmd));

		// if a select parent button was created and selected
		if (sVars.hasParameterKey(PARENTBUTTON)) {
			MyObject parent = target.getSingleParent(target);
			// switch the object to the parent
			target.find(parent.id);
			throw new EndOfInputRedoQueries(ret);
		}
		for (String thisKey : myVars.childToId.keySet()) {
			if (sVars.hasParameterKey(thisKey)) {
				MyObject tmp = target.getNew();
				tmp.find(myVars.childToId.get(thisKey));
				target.find(tmp.id);
				throw new EndOfInputRedoQueries(ret);
			}
		}

		ret.addAll(processDescendant(sVars, myVars));
		ret.addAll(processPeer(sVars, myVars));
		return ret;
	}

	protected FormsArray extractSpare(SessionVars sVars) throws Exception {
		return new FormsArray();
	}

//	@Override
//	public FormsArray callBack(SessionVars sVars, FormsMatrixDynamic calledInfo) throws Exception {
//		FormsArray ret = new FormsArray();
////		
////				switch (calledInfo.action) {
////				case DELETE:
////					break;
////				case DELETETEST:
////					try {
////						target.find(tmp.getId());
////					} catch (Exception e) {
////						ret.errorToUser(e);
//////						ret.addAll(updateTheCaller(sVars, target));
////						throw new EndOfInputException(ret);
////					}
////					try {
////						target.deleteTest();
////						ret.errorToUser(target.getLogicalName() + ":" + target.getInstanceName() + " has no children.");
//////						ret.addAll(updateTheCaller(sVars, target));
////						throw new EndOfInputException(ret);
////					} catch (Exception e) {
////						// target.clear();
////						ret.errorToUser(e);
//////						ret.addAll(updateTheCaller(sVars, target));
//////						return ret;
////						throw new EndOfInputException(ret);
////					}
////				case SELECT:
////					target.find(tmp.getId());
////					objs.clearChildren(index);
//////					calledInfo.status = FormsArray.STATUS.SELECTEDEXISTING;
////					ret.addAll(caller.callBack(sVars, calledInfo));
////					return ret;
////				default:
////					break;
////
////				}
////			}
////			ret.errorToUser("undefined object in calledInfo " + calledInfo.toString());
////			throw new EndOfInputException(ret);
////		}
//		return ret;
//	}

//	public FormsArray updateTheCaller(SessionVars sVars, MyObject oc) throws Exception {
//		FormsArray ret = new FormsArray();
//		ret.addAll(caller.callBack(sVars, new CalledInfo(this, true, FormsArray.STATUS.TRYAGAIN, oc)));
//		return ret;
//	}

//	FormsArray tryAgain(SessionVars sVars, CalledInfo oc) throws Exception {
//		FormsArray ret = new FormsArray();
//		ret.addAll(caller.callBack(sVars, oc));
//		return ret;
//	}

//	@Override
//	public void resetForm(INSTATE inState) {
//		super.resetForm(INSTATE.STANDARDFORM);
//	}

	// add a button to select the parent of the currently selected object
	public FormsArray parentButton(SessionVars sVars, MyVars myVars) {
		FormsArray ret = new FormsArray();
		MyObject parent = null;
		MyObject target = sVars.fmd.getObject();
		try {
			parent = target.getSingleParent(target);
			ret.rawText("parent of " + target.getInstanceName());
			ret.submitButton(parent.getInstanceName(), PARENTBUTTON);
			ret.newLine();
		} catch (Exception e) {
		}
		return ret;
	}

	public FormsArray addKidsButtons(SessionVars sVars, MyVars myVars) {
		FormsArray ret = new FormsArray();
		MyObject target = sVars.fmd.getObject();
		boolean kidsFound = false;
		// add buttons to select the children of the currently selected object
		myVars.childToId = new HashMap<String, Integer>();
		// can not have kids if not selected
		if (!target.isLoaded())
			return ret;
		try {
			for (MyObject child : target.listChildren(target)) {
				if (!kidsFound) {
					kidsFound = true;
					ret.rawText("Select a child of " + target.getInstanceName());
					ret.newLine();
				}
				String idString = Utils.getNextString();
				ret.submitButton(child.getInstanceName(), idString);
				myVars.childToId.put(idString, child.id);
			}
		} catch (Exception e) {
			ret.errorToUser(e);
		}
		return ret;
	}

	protected FormsArray addDescendant(SessionVars sVars, MyVars myVars) throws Exception {
		FormsArray ret = new FormsArray();
		MyObject target = sVars.fmd.getObject();
		if (target.isLoaded()) {
			// adding a sibling to target
			ret.newLine();
			ret.startTable();
			ret.rawText("Add a descendant to " + target.getInstanceName());
			myVars.descendant = target.getNew();
			ret.addAll(myVars.descendant.getNameForm(DESCENDANTBOX, myVars.descendant.isLoaded()));
			ret.endTable();
		} else {
			myVars.descendant = null;
		}
		return ret;
	}

	protected FormsArray addPeer(SessionVars sVars, MyVars myVars) throws Exception {
		FormsArray ret = new FormsArray();
		MyObject target = sVars.fmd.getObject();
		if (target.isLoaded()) {
			ret.startTable();
			ret.rawText("Add a peer to " + target.getInstanceName());
			myVars.peer = target.getNew();
			ret.addAll(myVars.peer.getNameForm(PEERBOX,
//					inState, 
					myVars.peer.isLoaded()));
			ret.endTable();
			// ret.submitButton("Add peer", "dgs");
		} else {
			myVars.peer = null;
		}
		return ret;
	}

	protected FormsArray processPeer(SessionVars sVars, MyVars myVars) throws Exception {
		FormsArray ret = new FormsArray();
		MyObject target = sVars.fmd.getObject();
		if (myVars.peer != null) {
			if (myVars.peer.nameChanged(sVars, PEERBOX)) {
				myVars.peer.extractName(sVars, PEERBOX);
				myVars.peer.doSanityUpdateAddTryAgain(sVars.fmd);
				MyObject parent = null;
				try {
					parent = target.getParents(target).iterator().next();
				} catch (Exception e) {
					// if we don't find a parent, target must be at the top
					// of the heap
				}
				// if target has a parent
				if (parent != null)
					parent.addChild(myVars.peer);
				throw new EndOfInputException(ret);
			}
		}
		return ret;
	}

	// allow location to override to get a warehouse for an anchor
	protected FormsArray processDescendant(SessionVars sVars, MyVars myVars)
			throws Exception, EndOfInputRedoQueries, EndOfInputException {
		FormsArray ret = new FormsArray();
		MyObject target = sVars.fmd.getObject();
		if (myVars.descendant != null) {
			if (myVars.descendant.nameChanged(sVars, DESCENDANTBOX)) {
				myVars.descendant.extractName(sVars, DESCENDANTBOX);
				myVars.descendant.doSanityUpdateAddTryAgain(sVars.fmd);
				target.addChild(myVars.descendant);
				target.find(myVars.descendant.id);
				throw new EndOfInputRedoQueries(ret);
			}
		}
		return ret;
	}

	static final String MYNAME = SelectForm.class.getCanonicalName();
}
