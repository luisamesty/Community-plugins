package com.ingeint.process;

import org.adempiere.util.ModelClassGenerator;
import org.adempiere.util.ModelInterfaceGenerator;
import org.compiere.model.MTable;

import com.ingeint.base.CustomProcess;
import com.ingeint.model.MModelGenerator;

public class ModelGenerator extends CustomProcess{

	@Override
	protected void prepare() {			
	}

	@Override
	protected String doIt() throws Exception {
		
		MModelGenerator mgen = new MModelGenerator(getCtx(), getRecord_ID(), get_TrxName());
		MTable table =null;
		
		if (mgen.getING_Table_ID()>0) {
			table = new MTable(getCtx(), mgen.getING_Table_ID(), get_TrxName());
			mgen.setTableName(table.getTableName());
		}
		
		ModelInterfaceGenerator.generateSource(mgen.getFolder(), mgen.getPackageName(), mgen.getEntityType(), mgen.getTableName());
		ModelClassGenerator.generateSource(mgen.getFolder(), mgen.getPackageName(), mgen.getEntityType(), mgen.getTableName());	
		
		addBufferLog(mgen.get_ID(), mgen.getCreated(),null,"@ModelGenerated@", mgen.get_Table_ID(),mgen.get_ID());
		return null;
	}
}
