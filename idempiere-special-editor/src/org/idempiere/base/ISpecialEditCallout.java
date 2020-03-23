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
package org.idempiere.base;

import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;

/**
 *  Special Edit Callout Interface
 * @author Carlos Ruiz
 *
 */
public interface ISpecialEditCallout
{
	/**
	 *	Can Edit
	 *  <p>
	 *	Special Edit callout's are used for validation of conditions that enable a
	 *	field to be edited after a document is processed
	 *  <p>
	 *
	 *  @param mTab     Model Tab
	 *  @param PO       The PO to be modified
	 *  @return boolean if the field can be edited
	 */
	public boolean canEdit (GridTab mTab, GridField mField, PO po);

	/**
	 *	Validate Edit
	 *  <p>
	 *	Business logic to be applied to the field being changed
	 *  <p>
	 *
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param PO       The PO to be modified
	 *  @param newValue The new value (selected in the special editor)
	 *  @return String  message or null if successful
	 */
	public String validateEdit (GridTab mTab, GridField mField, PO po, Object newValue);

	/**
	 *	Pre Edit
	 *  <p>
	 *	Business logic to be applied before the field is modified
	 *  (for example unpost)
	 *  <p>
	 *
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param PO       The PO to be modified
	 *  @return boolean if the pre-edition was successful
	 */
	public boolean preEdit (GridTab mTab, GridField mField, PO po);

	/**
	 *	On Edit
	 *  <p>
	 *	Business logic to modify the PO
	 *  <p>
	 *
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param PO       The PO to be modified
	 *  @param newValue The new value (selected in the special editor)
	 *  @return boolean if the pre-edition was successful
	 */
	public boolean updateEdit (GridTab mTab, GridField mField, PO po, Object newValue);

	/**
	 *	Pre Edit
	 *  <p>
	 *	Business logic to be applied after the field is modified
	 *  (for example repost)
	 *  <p>
	 *
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param PO       The PO to be modified
	 *  @return boolean if the post-edition was successful
	 */
	public boolean postEdit (GridTab mTab, GridField mField, PO po);

}
