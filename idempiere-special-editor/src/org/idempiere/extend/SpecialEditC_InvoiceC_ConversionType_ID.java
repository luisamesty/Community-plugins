/******************************************************************************
 * Copyright (C) 2015 Luis Amesty                                             *
 * Copyright (C) 2015 AMERP Consulting                                        *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 ******************************************************************************/
package org.idempiere.extend;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInvoice;
import org.compiere.model.PO;
import org.compiere.model.X_C_Invoice;
import org.compiere.util.Env;
import org.idempiere.base.ISpecialEditCallout;
import org.idempiere.base.SpecialEditorUtils;

/**
 * @author Nicolas Micoud 
 * Luis Amesty
 *
 */
public class SpecialEditC_InvoiceC_ConversionType_ID implements ISpecialEditCallout {
	
	String Message = "";
	
	@Override
	public boolean canEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("canEdit " + mTab + " - " + mField + " - "+ po);

		return true;
	}

	@Override
	public String validateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		System.out.println("validateEdit " + mTab + " - " + mField + " - "+ po);

		// User must select another charge (as an example, we forbid blank value)
		if (newValue == null)
			return "you must select an Conversion Type !";

		return null;
	}

	@Override
	public boolean preEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("preEdit " + mTab + " - " + mField + " - "+ po);
		SpecialEditorUtils.deletePosting(new MInvoice(Env.getCtx(), (Integer) mTab.getValue("C_Invoice_ID"), null));
		return true;
	}

	@Override
	public boolean updateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {

		X_C_Invoice inv = new X_C_Invoice(Env.getCtx(), mTab.getRecord_ID(), null);
		inv.setC_ConversionType_ID((Integer) newValue);
		inv.saveEx();
		return true;
	}

	@Override
	public boolean postEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("postEdit " + mTab + " - " + mField + " - "+ po);
		// Repost invoice
		SpecialEditorUtils.post(mTab, new MInvoice(Env.getCtx(), (Integer) mTab.getValue("C_Invoice_ID"), null));

		//Refresh
		SpecialEditorUtils.refresh(mTab);
		return true;
	}
}
