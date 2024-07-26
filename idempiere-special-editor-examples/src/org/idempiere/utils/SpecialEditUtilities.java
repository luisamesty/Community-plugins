package org.idempiere.utils;

import java.sql.Timestamp;

import org.adempiere.exceptions.DBException;
import org.compiere.model.MSysConfig;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class SpecialEditUtilities {

	/**
	 *  C_AllocationLine_C_Invoice
	 * @param p_C_Invoice_ID
	 * @return (TRUE IF INVOICE IS ALLOCATED)
	 */
	public boolean C_AllocationLine_C_Invoice (int p_C_Invoice_ID)
	
	{
		String sql;
		int C_AllocationLine_ID=0;
		boolean retValue=false;
		// 
    	sql = "SELECT DISTINCT C_AllocationLine_ID FROM C_AllocationLine WHERE C_Invoice_ID=?" ;
    	C_AllocationLine_ID = DB.getSQLValue(null, sql, p_C_Invoice_ID);	
		if (C_AllocationLine_ID > 0) {
			retValue=true;
		} else {
			retValue=false;
		}
    	return retValue;	
	}
	
	/**
	 * C_AllocationLine_C_Payment
	 * @param p_C_Payment_ID
	 * @param trxName
	 * @return  (TRUE IF PAYMENT IS ALLOCATED)
	 */
	public boolean C_AllocationLine_C_Payment (int p_C_Payment_ID)
	
	{
		String sql;
		int C_AllocationLine_ID=0;
		boolean retValue=false;
		// 
    	sql = "SELECT DISTINCT C_AllocationLine_ID FROM C_AllocationLine WHERE C_Payment_ID=?" ;
    	C_AllocationLine_ID = DB.getSQLValue(null, sql, p_C_Payment_ID);	
		if (C_AllocationLine_ID > 0) {
			retValue=true;
		} else {
			retValue=false;
		}
    	return retValue;	
	}
	
	/**
	 * sqlGet C_BankStatementLine_C_Payment (int p_C_Payment_ID)
	 * @param p_C_Invoice_ID	Invoice ID
	 * Verify if Invoice has C_Allocationline or C_Payment Records associated
	 * C_BankStatementLine_ID
	 */
	public boolean C_BankStatementLine_C_Payment (int p_C_Payment_ID)
	
	{
		String sql;
		int C_BankStatementLine_ID=0;
		boolean retValue=false;
		// LCO_InvoiceWHDocLines
    	sql = "SELECT DISTINCT C_BankStatementLine_ID FROM C_BankStatementLine WHERE C_Payment_ID=?" ;
    	C_BankStatementLine_ID = DB.getSQLValue(null, sql, p_C_Payment_ID);	
		if (C_BankStatementLine_ID > 0) {
			retValue=true;
		} else {
			retValue=false;
		}
    	return retValue;	
	}
	/**
	 *  updateLCO_InvoiceWithholdingDateAcct
	 * @param p_C_Invoice_ID
	 * @param p_DateAcct
	 * @param trxName
	 * @return
	 * @throws DBException
	 * 
	 * Notes:
	 * Temporary Made using DB access direct. 
	 * Model method will be used instead.
	 */
	public int updateLCO_InvoiceWithholdingDateAcct(int p_C_Invoice_ID, Timestamp p_DateAcct, String trxName)
	throws DBException
	{
		String sql;
		int no=0;
		if (! MSysConfig.getBooleanValue("LCO_USE_WITHHOLDINGS", false, Env.getAD_Client_ID(Env.getCtx())))
			return no;
		// SET  LCO_InvoiceWithholding
		sql = "UPDATE LCO_InvoiceWithholding SET DateAcct = '"+p_DateAcct+"' WHERE  C_Invoice_ID="+p_C_Invoice_ID;	
		System.out.println("sql="+sql);
		no = DB.executeUpdateEx(sql, trxName);
		return no;
	}
	
	/**
	 *  updateLCO_InvoiceWithholdingDateAcct
	 * @param p_C_Invoice_ID
	 * @param p_DateTrx
	 * @param trxName
	 * @return
	 * @throws DBException
	 * 
	 * Notes:
	 * Temporary Made using DB access direct. 
	 * Model method will be used instead.
	 */
	public int updateLCO_InvoiceWithholdingDateInvoiced(int p_C_Invoice_ID, Timestamp p_DateTrx, String trxName)
	throws DBException
	{
		String sql;
		int no=0;
		if (! MSysConfig.getBooleanValue("LCO_USE_WITHHOLDINGS", false, Env.getAD_Client_ID(Env.getCtx())))
			return no;
		// SET  LCO_InvoiceWithholding
		sql = "UPDATE LCO_InvoiceWithholding SET DateTrx = '"+p_DateTrx+"' WHERE  C_Invoice_ID="+p_C_Invoice_ID;	
		System.out.println("sql="+sql);
		no = DB.executeUpdateEx(sql, trxName);
		return no;
	}
}
