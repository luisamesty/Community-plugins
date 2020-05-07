package org.idempiere.extend;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MPayment;
import org.compiere.model.PO;
import org.compiere.model.X_C_Payment;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.idempiere.base.ISpecialEditCallout;
import org.idempiere.base.SpecialEditorUtils;
import org.idempiere.utils.SpecialEditUtilities;

public class SpecialEditC_PaymentDateTrx implements ISpecialEditCallout {
	
	String Message = "";
	
	@Override
	public boolean canEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("canEdit " + mTab + " - " + mField + " - "+ po);
		
		// The invoice date
		if (mTab.getValue("DateTrx") == null)
			return false;

		return true;
	}

	@Override
	public String validateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		System.out.println("validateEdit " + mTab + " - " + mField + " - "+ po);
		X_C_Payment inv = new X_C_Payment(Env.getCtx(), mTab.getRecord_ID(), null);
		// Verify C_AllocationLine
		SpecialEditUtilities sea = new SpecialEditUtilities();
		boolean isAllocPay = sea.C_AllocationLine_C_Payment(mTab.getRecord_ID());
		// Verify C_BankStatementLine
		boolean isBSLPay = sea.C_BankStatementLine_C_Payment(mTab.getRecord_ID());
		if (isAllocPay || isBSLPay) {
			Message = "*** "+Msg.translate(Env.getCtx(),"invalid")+" **** \r\n";
			if (isAllocPay)
				Message= Message+Msg.translate(Env.getCtx(),"PaymentIsAllocated"+" \r\n");
			if (isBSLPay)
				Message = Message + Msg.translate(Env.getCtx(),"PaymentIsReconcilled"+" \r\n");
			return "Error !!!"+"  "+Message;
		}
		Timestamp DateEntered = (Timestamp) newValue ;
		Timestamp DateAcct = inv.getDateAcct();
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
		Timestamp actualDateTrx = inv.getDateTrx();
		if (DateEntered.before(FirstDate) || DateEntered.after(MaxDate)) {
			String Message = "*** "+Msg.translate(Env.getCtx(),"invalid")+" **** "+
					" ("+Msg.translate(Env.getCtx(),"From")+"="+FirstDate.toString().substring(0,10)+")"+
					" ("+Msg.translate(Env.getCtx(),"to")+"="+MaxDate.toString().substring(0,10)+")";
			mTab.setValue("DateTrx", actualDateTrx);
			newValue=actualDateTrx;
			return "Error !!!"+"   /r/n"+Message;
		} else {
			return null;			
		}
	}

	@Override
	public boolean preEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("preEdit " + mTab + " - " + mField + " - "+ po);
		//  Delete Posting
		SpecialEditorUtils.deletePosting(new MPayment(Env.getCtx(), (Integer) mTab.getValue("C_Payment_ID"), null));
		return true;
	}

	@Override
	public boolean updateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		//		po.set_ValueOfColumn("C_Charge_ID", newValue);
		//		po.saveEx(); it can't be done using PO as you can't save a MInvoiceLine when its parent is processed
		X_C_Payment inv = new X_C_Payment(Env.getCtx(), mTab.getRecord_ID(), null);
		inv.setDateTrx((Timestamp) newValue);
		inv.saveEx();

		return true;
	}

	@Override
	public boolean postEdit(GridTab mTab, GridField mField, PO po) {
		// Repost invoice
		SpecialEditorUtils.post(mTab, new MPayment(Env.getCtx(), (Integer) mTab.getValue("C_Payment_ID"), null));
		//Refresh
		SpecialEditorUtils.refresh(mTab);
		return true;
	}

}
