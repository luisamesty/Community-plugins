package org.idempiere.utils;

import org.compiere.util.DB;

public class SpecialEditUtilities {

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
}
