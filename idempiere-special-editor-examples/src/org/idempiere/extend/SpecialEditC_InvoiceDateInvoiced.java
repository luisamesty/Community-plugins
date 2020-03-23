package org.idempiere.extend;

import java.sql.Timestamp;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInvoice;
import org.compiere.model.PO;
import org.compiere.model.X_C_Invoice;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.idempiere.base.ISpecialEditCallout;
import org.idempiere.base.SpecialEditorUtils;

public class SpecialEditC_InvoiceDateInvoiced implements ISpecialEditCallout {

	@Override
	public boolean canEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("canEdit " + mTab + " - " + mField + " - "+ po);
		
		// The invoice date
		if (mTab.getValue("DateInvoiced") == null)
			return false;

		return true;
	}

	@Override
	public String validateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		System.out.println("validateEdit " + mTab + " - " + mField + " - "+ po);
		X_C_Invoice inv = new X_C_Invoice(Env.getCtx(), mTab.getRecord_ID(), null);
		Timestamp DateEntered = (Timestamp) newValue ;
		Timestamp DateAcct = inv.getDateAcct();
		if (DateEntered.after(DateAcct)) {
			String Message = Msg.translate(Env.getCtx(),"invalid")+" ("+Msg.translate(Env.getCtx(),"DateInvoice")+
					"="+DateEntered+")";
			//return "Error !!!";
		}
		return null;
	}

	@Override
	public boolean preEdit(GridTab mTab, GridField mField, PO po) {
		System.out.println("preEdit " + mTab + " - " + mField + " - "+ po);
		//  Delete Posting
		SpecialEditorUtils.deletePosting(new MInvoice(Env.getCtx(), (Integer) mTab.getValue("C_Invoice_ID"), null));
		return true;
	}

	@Override
	public boolean updateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		//		po.set_ValueOfColumn("C_Charge_ID", newValue);
		//		po.saveEx(); it can't be done using PO as you can't save a MInvoiceLine when its parent is processed

		X_C_Invoice inv = new X_C_Invoice(Env.getCtx(), mTab.getRecord_ID(), null);
		inv.setDateInvoiced((Timestamp) newValue);
		inv.saveEx();

		return true;
	}

	@Override
	public boolean postEdit(GridTab mTab, GridField mField, PO po) {

		// Repost invoice
		SpecialEditorUtils.post(mTab, new MInvoice(Env.getCtx(), (Integer) mTab.getValue("C_Invoice_ID"), null));

		//Refresh
		SpecialEditorUtils.refresh(mTab);
		return true;
	}

}
