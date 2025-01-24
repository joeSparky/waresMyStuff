package com.parts.security;

import java.sql.ResultSet;
import com.db.DoubleString;
import com.db.DoubleStrings;
import com.db.SessionVars;
import com.db.Strings;
import com.security.MyObject;

/**
 * This class adds quantity to the link between parts and locations. This allows
 * more than one part to be stored at a single location. This also allows parts
 * to be parents of locations, as a child (location) can only have one parent
 * (part), with the quantity field providing the number of parts.
 * 
 * @author Joe
 *
 */
public class PartLink extends InventoryDate {
	/**
	 * column name
	 */
	public static final String QUANTITY = "quantity";

	public PartLink(MyObject part, MyObject location, SessionVars sVars) throws Exception {
		super(part, location, sVars);
		clear();
	}

	private static int MAGICNUMBER = -394;

	protected void clear() {
		super.clear();
	}

	private int quantity = MAGICNUMBER;

	public void setLinkQuantity(int quant) {
		quantity = quant;
	}

	@Override
	public PartLink add() throws Exception {
		if (quantity == MAGICNUMBER)
			throw new Exception("quantity not set.<br>" + new Exception().getStackTrace()[0]);
		// when a new item is added to the warehouse, mark it as inventoried
		super.setInventoried(true);
		super.add();
		return this;
	}

	public PartLink updateAddQuantity(int quantity, boolean inventoried) throws Exception {
		super.setInventoried(inventoried);
		this.quantity += quantity;
		if (this.quantity == 0) {
//			deleteTest();
			deleteUnconditionally();
		} else
			update();
		return this;
	}

	public PartLink updateSetQuantity(int quantity, boolean inventoried) throws Exception {
		super.setInventoried(inventoried);
		this.quantity = quantity;
		if (this.quantity == 0) {
//			deleteTest();
			deleteUnconditionally();
		} else
			update();
		return this;
	}

	@Override
	protected PartLink extractInfo(ResultSet row) throws Exception {
		quantity = row.getInt(QUANTITY);
		super.extractInfo(row);
		return this;
	}

	@Override
	public DoubleStrings extendAdd() {
		DoubleStrings ret = new DoubleStrings();
		ret.add(new DoubleString(QUANTITY, "" + quantity));
		ret.addAll(super.extendAdd());
		return ret;
	}

	@Override
	public DoubleStrings extendUpdate() {
		return extendAdd();
	}

	@Override
	public Strings extendNewTable() {
		Strings strs = new Strings();
		strs.add("`quantity` int(11)");
		strs.addAll(super.extendNewTable());
		return strs;
	}

	public PartLink find() throws Exception {
		super.find();
		return this;
	}

	public int getLinkQuantity() {
		return quantity;
	}

	@Override
	public boolean hasQuantity() {
		return true;
	}
}
