package com.parts.forms;

import com.db.SessionVars;
import com.db.XML;
import com.forms.FormsArray;
import com.forms.FormsMatrixDynamic;
import com.forms.SelectForm;
import com.forms.Utils;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.parts.security.PartLink;
import com.reports.StringWriter;
import com.security.MyObject;
import com.security.MyObjects;

/**
 * generate a list of comma separated values of parts in inventory
 * 
 * @author Joe
 *
 */
public class CSVFormSelect extends SelectForm {
/**
	 * 
	 */
	private static final long serialVersionUID = 8857587138449204316L;
SessionVars sVars = null;
	public CSVFormSelect(SessionVars sVars) throws Exception {
		super(sVars, CSVFormSelect.class.getCanonicalName());
		this.sVars = sVars;
//		this.fmd = fmd;
		this.row = fmd.row;
		this.column = fmd.column;
//		this.objs = fmd;
//		this.index = index;
	}

	FormsMatrixDynamic fmd = null;
	int row, column = -1;

//	SearchTargets objs = null;
//	int index = -1;

	public String DUMPINVENTORY = Utils.getNextString();

	@Override
	public FormsArray getForm(SessionVars sVars) {
		FormsArray ret = new FormsArray();
		ret.submitButton("Generate CSV file of inventory", DUMPINVENTORY);
		ret.cancelButton();
//		ret.setReturnTo(DispatchRunStuff.getMyGoTo(this), this, sVars);
		return ret;
	}

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		if (sVars.hasParameterKey(DUMPINVENTORY)) {
//			SearchTargets sts = fmd.get(row);
			
			StringWriter sw = new StringWriter(sVars);
			sw.writeString("inventory.csv", dumpInventory());
//			XML xml = new XML();
			ret.errorToUser(
					"Inventory written to " + sVars.xml.readXML(XML.CSVPATH) + sVars.xml.getSeparator(sVars) + "inventory.csv");
		}
		return ret;
	}

	public String dumpInventory() throws Exception {
		String ret = "part name, quantity\n";
		MyObjects objs = new Part(sVars).listAll();
		for (MyObject obj : objs) {
			int quant = 0;
			MyObjects children = obj.listChildren(new Location(sVars));
			for (MyObject child : children) {
				quant += new PartLink(obj, child, sVars).find().getLinkQuantity();
			}
			ret += obj.getInstanceName() + ", " + "" + quant + "\n";
		}
		return ret;
	}
}
