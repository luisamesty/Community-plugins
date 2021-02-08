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
import org.compiere.model.MInOutLine;
import org.compiere.model.PO;
import org.compiere.model.X_M_InOut;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.idempiere.base.ISpecialEditCallout;
import org.idempiere.base.SpecialEditorUtils;

/**
 * @author Nicolas Micoud 
 * Luis Amesty
 *
 */
public class SpecialEditM_InOutMovementDate implements ISpecialEditCallout {
	
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
		Timestamp MovementDate = mio.getMovementDate();
		// Initiate Timestamp Variables
		Timestamp FirstDate = TimeUtil.getMonthFirstDay(MovementDate);
		Timestamp MaxDate = TimeUtil.getMonthFirstDay(MovementDate);
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
		Timestamp actualMovementDate = mio.getMovementDate();
		if (DateEntered.before(FirstDate) || DateEntered.after(MaxDate)) {
			String Message = "*** "+Msg.translate(Env.getCtx(),"invalid")+" **** "+
					" ("+Msg.translate(Env.getCtx(),"From")+"="+FirstDate.toString().substring(0,10)+")"+
					" ("+Msg.translate(Env.getCtx(),"to")+"="+MaxDate.toString().substring(0,10)+")";
			mTab.setValue("MovementDate", actualMovementDate);
			newValue=actualMovementDate;
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

		MInOut min = new MInOut(Env.getCtx(), mTab.getRecord_ID(), null);
		min.setMovementDate((Timestamp) newValue);
		min.saveEx();
		// UPDATE Other Tables
		//SpecialEditUtilities sea = new SpecialEditUtilities();
		//sea.updateLCO_InvoiceWithholdingDateAcct(inv.getC_Invoice_ID(), inv.getDateAcct(), null);
		// UPDATE M_Transaction (MovementDate)
		MInOutLine[] minLines = min.getLines();
		int count = 0;
		for (int i = 0; i < minLines.length; i++)
		{
			MInOutLine mioline = minLines[i];
			updateMTransaction(mioline.getM_InOutLine_ID(), min.getMovementDate());

		}
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
	
	public void updateMTransaction(int M_InOutLine_ID, Timestamp MovementDate) {
		//Completar
		String sql;
//		Integer M_Transaction_ID = 0;
//		// M_Transaction_ID
//    	sql = "select M_Transaction_ID from M_Transaction WHERE M_InOutLine_ID=?" ;
//    	M_Transaction_ID = DB.getSQLValue(null, sql, M_InOutLine_ID);	
    	// Update MovementDate
    	sql = "UPDATE M_Transaction SET MovementDate='"+MovementDate+"' WHERE M_InOutLine_ID="+M_InOutLine_ID;
    	DB.executeUpdateEx(sql, null);
    	System.out.println(sql);
	}
}
