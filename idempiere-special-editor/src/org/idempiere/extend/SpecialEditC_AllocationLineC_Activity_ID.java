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
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.PO;
import org.compiere.model.X_C_AllocationLine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.base.ISpecialEditCallout;
import org.idempiere.base.SpecialEditorUtils;

/**
 * @author Nicolas Micoud 
 * Luis Amesty
 *
 */
public class SpecialEditC_AllocationLineC_Activity_ID implements ISpecialEditCallout {
	
	String Message = "";
	int C_Activity_ID=0;
	int C_AllocationHdr_ID=0;
	int C_AllocationLine_ID=0;

	@Override
	public boolean canEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("canEdit " + mTab + " - " + mField + " - "+ po);

//		if (mTab.getValue("C_Activity_ID") == null) {
//
//			return false;
//		}
		return true;
	}

	@Override
	public String validateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		System.out.println("validateEdit " + mTab + " - " + mField + " - "+ po);

		// User must select another charge (as an example, we forbid blank value)
		if (newValue == null)
			return "you must select an activity !";
		C_Activity_ID = (int) newValue;
		return null;
	}

	@Override
	public boolean preEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("preEdit " + mTab + " - " + mField + " - "+ po);
		
		SpecialEditorUtils.deletePosting(new MAllocationHdr(Env.getCtx(), (Integer) mTab.getValue("C_AllocationHdr_ID"), null));
		return true;
	}

	@Override
	public boolean updateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		System.out.println("updateEdit " + mTab + " - " + mField + " - "+ po);
//
//		System.out.println("updateEdit 2 set C_Activity_ID =" + C_Activity_ID);
//		mallocline.setC_Activity_ID(C_Activity_ID);
//		mallocline.saveEx();
//
//		System.out.println("updateEdit 3 get C_Activity_ID =" + C_Activity_ID+"  C_AllocationHdr_ID="+C_AllocationHdr_ID+"   C_AllocationLine_ID="+C_AllocationLine_ID);
//
	
//		MAllocationLine mallocline = new MAllocationLine(Env.getCtx(), mTab.getRecord_ID(), null);
//		I_C_AllocationLine_Amerp amallocline =  POWrapper.create(mallocline, I_C_AllocationLine_Amerp.class);
//				
//		System.out.println("updateEdit 2 set C_Activity_ID =" + C_Activity_ID);
//		amallocline.setC_Activity_ID(C_Activity_ID);
//		((PO) amallocline).saveEx();
//		System.out.println("updateEdit 3 get C_Activity_ID =" + amallocline.getC_Activity_ID());

//		C_AllocationHdr_ID= (Integer) mTab.getValue("C_AllocationHdr_ID");
//		C_AllocationLine_ID= mTab.getRecord_ID();
//		String sql3 = "UPDATE C_AllocationLine "
//			+ " SET C_Activity_ID="+C_Activity_ID +" "  
//			+ " WHERE C_AllocationHdr_ID =" + C_AllocationHdr_ID + " "
//			+ " AND C_AllocationLine_ID =" + C_AllocationLine_ID + " " ;
//      	System.out.println("SQL="+sql3);
//      	DB.executeUpdateEx(sql3, null);
      	
		MAllocationLine all = new MAllocationLine(Env.getCtx(), mTab.getRecord_ID(), null);
		all.set_ValueOfColumn("C_Activity_ID", C_Activity_ID);
		all.saveEx();
		return true;
	}

	@Override
	public boolean postEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("postEdit " + mTab + " - " + mField + " - "+ po);
		// Repost invoice
		SpecialEditorUtils.post(mTab, new MAllocationHdr(Env.getCtx(), (Integer) mTab.getValue("C_AllocationHdr_ID"), null));

		//Refresh
		SpecialEditorUtils.refresh(mTab);
		return true;
	}
}
