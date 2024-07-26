/******************************************************************************
 * Copyright (C) 2012 Carlos Ruiz                                             *
 * Copyright (C) 2012 Trek Global                 							  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.idempiere.extend;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.idempiere.base.ISpecialEditCallout;

/**
 * @author Carlos Ruiz
 *
 */
public class SpecialEditC_OrderC_Project_ID implements ISpecialEditCallout {

	@Override
	public boolean canEdit(GridTab mTab, GridField mField, PO po) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String validateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean preEdit(GridTab mTab, GridField mField, PO po) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateEdit(GridTab mTab, GridField mField, PO po, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean postEdit(GridTab mTab, GridField mField, PO po) {
		// TODO Auto-generated method stub
		return false;
	}

}
