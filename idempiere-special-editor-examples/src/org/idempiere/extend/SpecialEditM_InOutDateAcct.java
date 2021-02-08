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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.PO;
import org.compiere.model.X_C_Invoice;
import org.compiere.model.X_M_InOut;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.idempiere.base.ISpecialEditCallout;
import org.idempiere.base.SpecialEditorUtils;
import org.idempiere.utils.SpecialEditUtilities;

/**
 * @author Luis Amesty
 *
 */
public class SpecialEditM_InOutDateAcct implements ISpecialEditCallout {
	
	String Message = "";
	
	@Override
	public boolean canEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("canEdit " + mTab + " - " + mField + " - "+ po);

		return true;
	}

	@Override
	public String validateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {

		System.out.println("validateEdit " + mTab + " - " + mField + " - "+ po);
		X_M_InOut mio = new X_M_InOut(Env.getCtx(), mTab.getRecord_ID(), null);

		Timestamp DateEntered = (Timestamp) newValue ;
		Timestamp DateAcct = mio.getDateAcct();
		// Initiate Timestamp Variables
		Timestamp FirstDate = TimeUtil.getMonthFirstDay(DateAcct);
		Timestamp MaxDate = TimeUtil.getMonthFirstDay(DateAcct);
		// Calculates Last Day of Current Next Month
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(MaxDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MONTH, 2);
		cal.add(Calendar.DATE, -1);
		MaxDate = new Timestamp (cal.getTimeInMillis());
		// Calculates First Day of Next Previus Month
		cal.setTime(FirstDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MONTH, -1);
		FirstDate = new Timestamp (cal.getTimeInMillis());
		// GET DAteInvoiced
		System.out.println("FirstDate="+FirstDate+"   Maxdate="+MaxDate);
		Timestamp actualDateAcct = mio.getDateAcct();
		if (DateEntered.before(FirstDate) || DateEntered.after(MaxDate)) {
			String Message = "*** "+Msg.translate(Env.getCtx(),"invalid")+" **** "+
					" ("+Msg.translate(Env.getCtx(),"From")+"="+FirstDate.toString().substring(0,10)+")"+
					" ("+Msg.translate(Env.getCtx(),"to")+"="+MaxDate.toString().substring(0,10)+")";
			mTab.setValue("DateAcct", actualDateAcct);
			newValue=actualDateAcct;
			return "Error !!!"+"   /r/n"+Message;
		} else {
			return null;			
		}

	}

	@Override
	public boolean preEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("preEdit " + mTab + " - " + mField + " - "+ po);
		SpecialEditorUtils.deletePosting(new MInOut(Env.getCtx(), (Integer) mTab.getValue("M_InOut_ID"), null));
		return true;
	}

	@Override
	public boolean updateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {

		X_M_InOut min = new X_M_InOut(Env.getCtx(), mTab.getRecord_ID(), null);
		min.setDateAcct((Timestamp) newValue);
		min.saveEx();
		// UPDATE Other Tables
		//SpecialEditUtilities sea = new SpecialEditUtilities();
		//sea.updateLCO_InvoiceWithholdingDateAcct(inv.getC_Invoice_ID(), inv.getDateAcct(), null);
		return true;

	}

	@Override
	public boolean postEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("postEdit " + mTab + " - " + mField + " - "+ po);
		// Repost invoice
		SpecialEditorUtils.post(mTab, new MInOut(Env.getCtx(), (Integer) mTab.getValue("M_InOut_ID"), null));

		//Refresh
		SpecialEditorUtils.refresh(mTab);
		return true;
	}
}
