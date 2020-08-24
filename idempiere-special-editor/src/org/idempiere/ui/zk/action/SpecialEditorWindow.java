/******************************************************************************
 * Copyright (C) 2012 Carlos Ruiz                                             *
 * Copyright (C) 2012 Trek Global                                             *
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

import java.sql.Timestamp;
import java.util.List;

import org.adempiere.base.Service;
import org.adempiere.base.ServiceQuery;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.idempiere.base.ISpecialEditCallout;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vbox;

/**
 * @author Carlos Ruiz
 *
 */
public class SpecialEditorWindow extends Window implements EventListener<Event>, ValueChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 66664724163845835L;
	private String retMessage = "";
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);
	private Listbox enabledFields = new Listbox();
	private Div divField = new Div();
	private Hbox hbField = new Hbox();
	private WEditor editor = null;
	private Label messageLabel = new Label();
	public Textbox messageField = new Textbox();
	Hbox hb = new Hbox();
    Hbox hbm = new Hbox();
    
	/**
	 *
	 */
	public SpecialEditorWindow() {
	}

	public void init(ADWindowContent panel) {
		enabledFields.setMold("select");
		enabledFields.getItems().clear();

		GridTab tab = panel.getActiveGridTab();
		PO po = tab.getTableModel().getPO(tab.getCurrentRow());
		for (GridField field : tab.getFields()) {
// OJO TRAZA
//System.out.println("Field: " + field.getColumnName() +" = "+field.getValue() +"  isDisplayed: " + field.isDisplayed(true) +
//	" isAlwaysUpdateable = "+ field.isAlwaysUpdateable());
			if (   field.isDisplayed(true)
				&& !field.isAlwaysUpdateable()
				&& !field.isReadOnly()
				&& field.getDisplayType() != DisplayType.Button) {
				if (canEdit(tab, field, po)) {
// OJO TRAZA
//System.out.println("Field: " + field.getColumnName() +" (OK for edit) = "+field.getValue() + 
//		"  isDisplayed = " + field.isDisplayed(true) +
//		" isAlwaysUpdateable = "+ field.isAlwaysUpdateable());
//System.out.println("  ...OK for edit");
					enabledFields.appendItem(field.getHeader(), field);
				}
			}
		}
		enabledFields.setSelectedItem(null);
		enabledFields.addEventListener(Events.ON_SELECT, this);

		setTitle(Msg.getMsg(Env.getCtx(), "SpecialEdit") + ": " + panel.getActiveGridTab().getName());
		setWidth("650px");
		setClosable(true);
		setSizable(true);
		setBorder("normal");
		setStyle("position:absolute");

		Vbox vb = new Vbox();
		vb.setWidth("100%");
		appendChild(vb);

		Div div = new Div();
		div.setStyle("float: right");
		if (enabledFields.getItemCount() == 0) {
			div.appendChild(new Label(Msg.getMsg(Env.getCtx(), "NoFieldsForSpecialEdit")));
			hb.appendChild(div);
			vb.appendChild(hb);

			divField.setStyle("float: right");
			hbField.appendChild(divField);
			vb.appendChild(hbField);

			confirmPanel = new ConfirmPanel(false);
			vb.appendChild(confirmPanel);
			confirmPanel.addActionListener(this);
		} else {
			div.appendChild(new Label(Msg.getElement(Env.getCtx(), "AD_Field_ID")));
			hb.appendChild(div);
			hb.appendChild(enabledFields);
			enabledFields.setWidth("100%");
			vb.appendChild(hb);

			divField.setStyle("float: right");
			divField.setWidth("250px");
			hbField.appendChild(divField);
			vb.appendChild(hbField);
			
			// Message Field
			
			Vbox vbm = new Vbox();
			vbm.setWidth("100%");
			appendChild(vbm);
			Div divm = new Div();
			divm.appendChild(new Label(Msg.getMsg(Env.getCtx(), "Message")));
			hbm.appendChild(divm);
			vbm.appendChild(hbm);
			hbm.appendChild(messageField);
			messageField.setWidth("100%");
			messageLabel.setText(Msg.translate(Env.getCtx(), "Message"));
			messageField.setText("");
			messageField.setWidth("450px");
			messageField.setReadonly(true);
			messageField.setStyle("text-align: left");
			messageField.setValue(Msg.getMsg(Env.getCtx(), "RequiredEnter"));
			// Message Field
			
			confirmPanel = new ConfirmPanel(true);
			vb.appendChild(confirmPanel);
			confirmPanel.addActionListener(this);
			confirmPanel.getButton("Ok").setEnabled(false);
		}

		setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		Object source = event.getTarget();
		if (event.getTarget().equals(confirmPanel.getButton("Ok"))) {
			if (enabledFields.getItemCount() > 0) {
				GridField mField = (GridField) enabledFields.getSelectedItem().getValue();
				GridTab mTab = mField.getGridTab();
				PO po = mTab.getTableModel().getPO(mTab.getCurrentRow());
				Object newValue = editor.getValue();
				update(mTab, mField, po, newValue);
				// Set Message to Text Field
				messageField.setValue(retMessage);
				// 
			}
			onClose();

		} else if (event.getTarget().equals(confirmPanel.getButton("Cancel"))) {
			onClose();
		} else if (source == enabledFields) {
//			enabledFields.setEnabled(false); ndNico : why that ? you may have selected the wrong item
			paintField((GridField) enabledFields.getSelectedItem().getValue());
		}
	}

	private void paintField(GridField field) {
		if (editor != null)
			hb.removeChild(editor.getComponent());
		editor = WebEditorFactory.getEditor(null, field, false);
		hb.appendChild(editor.getComponent());
		editor.setValue(field.getValue());
		editor.addValueChangeListener(this);
		confirmPanel.getButton("Ok").setEnabled(false);
	}

	private void update(GridTab mTab, GridField mField, PO po, Object newValue) {
		// Code to call preEdit, updateEdit, postEdit
		
		List<ISpecialEditCallout> callouts = findCallout(mTab.getTableName(), mField.getColumnName());
		if (callouts != null && !callouts.isEmpty()) {
			ISpecialEditCallout co = callouts.get(0);

			if (!co.preEdit(mTab, mField, po)) {
				throw new AdempiereException("error in preEdit : " + co);
			} else {
				if (!co.updateEdit(mTab, mField, po, newValue)) {
					throw new AdempiereException("error in updateEdit : " + co);
				} else {
					if (!co.postEdit(mTab, mField, po)) {
						throw new AdempiereException("error in postEdit : " + co);
					} else {
						System.out.println("Record Updated !!!");
					}
				}
			}
		}
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {

		confirmPanel.getButton("Ok").setEnabled(false);
		
		GridField mField = (GridField) enabledFields.getSelectedItem().getValue();
		GridTab mTab = mField.getGridTab();
		PO po = mTab.getTableModel().getPO(mTab.getCurrentRow());
		Object newValue = editor.getValue();
		
		List<ISpecialEditCallout> callouts = findCallout(mTab.getTableName(), mField.getColumnName());
		if (callouts != null && !callouts.isEmpty()) {
			ISpecialEditCallout co = callouts.get(0);
			
			String err = co.validateEdit(mTab, mField, po, newValue);
			if (!Util.isEmpty(err)) {
				Clients.showNotification(err, "error", this, "overlap/top_lef", 2000, true);
				// Return Message to Form
				messageField.setValue(err);
				// 
				return;
			}
		}
		
		confirmPanel.getButton("Ok").setEnabled(true);
	}

	/**
	 *
	 * @param tableName
	 * @param columnName
	 * @return list of callout register for tableName.columnName
	 */
	private List<ISpecialEditCallout> findCallout(String tableName, String columnName) {
		ServiceQuery query = new ServiceQuery();
		query.put("tableName", tableName);
		query.put("columnName", columnName);

		return Service.locator().list(ISpecialEditCallout.class, query).getServices();
	}

	private boolean canEdit(GridTab mTab, GridField mField, PO po) {
		List<ISpecialEditCallout> callouts = findCallout(mTab.getTableName(), mField.getColumnName());
		if (callouts != null && !callouts.isEmpty()) {
			for (ISpecialEditCallout co : callouts)
			{
				if (!co.canEdit(mTab, mField, po)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public Textbox getMessageField() {
		return messageField;
	}

	public void setMessageField(Textbox messageField) {
		this.messageField = messageField;
		
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

}
