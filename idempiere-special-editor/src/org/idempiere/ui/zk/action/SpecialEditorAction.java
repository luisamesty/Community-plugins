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
package org.idempiere.ui.zk.action;

import org.adempiere.webui.action.IAction;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.apps.AEnv;

/**
 * @author Carlos Ruiz
 *
 */
public class SpecialEditorAction implements IAction {

	/**
	 * default constructor
	 */
	public SpecialEditorAction() {
	}

	/* (non-Javadoc)
	 * @see org.adempiere.webui.action.IAction#execute(java.lang.Object)
	 */
	@Override
	public void execute(Object target) {
		ADWindow adwindow = (ADWindow) target;
		ADWindowContent panel = adwindow.getADWindowContent();

		doSpecialEdit(panel);
	}

	private void doSpecialEdit(ADWindowContent panel) {
		SpecialEditorWindow window = new SpecialEditorWindow();
		window.init(panel);
	
		AEnv.showWindow(window);
	}
}
