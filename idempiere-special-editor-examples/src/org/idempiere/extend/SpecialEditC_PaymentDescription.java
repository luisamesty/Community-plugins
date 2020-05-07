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

public class SpecialEditC_PaymentDescription implements ISpecialEditCallout {

	@Override
	public boolean canEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("canEdit " + mTab + " - " + mField + " - "+ po);
		
		return true;
	}

	@Override
	public String validateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		System.out.println("validateEdit " + mTab + " - " + mField + " - "+ po);
		X_C_Payment pay = new X_C_Payment(Env.getCtx(), mTab.getRecord_ID(), null);
		String DescriptionEntered = (String) newValue;
		
		if (newValue == null) {
			String Message = "*** "+Msg.translate(Env.getCtx(),"invalid")+" **** "+
					" ("+Msg.translate(Env.getCtx(),"Description")+"="+DescriptionEntered+")";
			return "Error !!!"+"   /r/n"+Message;
		}
		return null;
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
		X_C_Payment pay = new X_C_Payment(Env.getCtx(), mTab.getRecord_ID(), null);
		pay.setDescription((String) newValue);
		pay.saveEx();

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
