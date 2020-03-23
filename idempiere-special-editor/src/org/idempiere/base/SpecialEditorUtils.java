package org.idempiere.base;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridTab;
import org.compiere.model.MFactAcct;
import org.compiere.model.PO;

public class SpecialEditorUtils {

	/** Refresh current and parents tabs */
	public static void refresh(GridTab mTab)
	{
		mTab.dataRefreshAll();
		mTab.refreshParentTabs();
	}

	/** Delete posting related to po and set Posted column to false */
	public static void deletePosting(PO po)
	{
		MFactAcct.deleteEx (po.get_Table_ID(), po.get_ID(), null);
		po.set_ValueOfColumn("Posted", false);
		po.saveEx();
	}

	/** Generate posting for po */
	public static void post(GridTab mTab, PO po)
	{
		String error = AEnv.postImmediate (mTab.getWindowNo(), po.getAD_Client_ID(), po.get_Table_ID(), po.get_ID(), true);
		if (error != null)
			FDialog.error(0, null, "PostingError-N", error);
	}
}
