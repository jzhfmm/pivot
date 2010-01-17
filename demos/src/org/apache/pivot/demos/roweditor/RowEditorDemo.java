/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.demos.roweditor;

import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.EnumList;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.content.CalendarDateSpinnerData;
import org.apache.pivot.wtk.content.TableViewRowEditor;
import org.apache.pivot.wtk.skin.CardPaneSkin;
import org.apache.pivot.wtkx.WTKXSerializer;

/**
 * Demonstrates a flip transition used to initiate a table view row editor.
 */
public class RowEditorDemo implements Application {
    private Window window = null;
    private TableView tableView = null;

    @Override
    public void startup(Display display, Map<String, String> properties)
        throws Exception {
        WTKXSerializer wtkxSerializer = new WTKXSerializer();
        window = (Window)wtkxSerializer.readObject(this, "demo.wtkx");
        tableView = (TableView)wtkxSerializer.get("tableView");

        TableViewRowEditor tableViewRowEditor = new TableViewRowEditor();
        tableViewRowEditor.setEditEffect(CardPaneSkin.SelectionChangeEffect.HORIZONTAL_SLIDE);
        tableView.setRowEditor(tableViewRowEditor);

        // Date uses a Spinner with a CalendarDateSpinnerData model
        Spinner dateSpinner = new Spinner(new CalendarDateSpinnerData());
        dateSpinner.setSelectedItemKey("date");
        tableViewRowEditor.getCellEditors().put("date", dateSpinner);

        // Expense type uses a ListButton that presents the expense types
        ListButton typeListButton = new ListButton(new EnumList<ExpenseType>(ExpenseType.class));
        typeListButton.setSelectedItemKey("type");
        tableViewRowEditor.getCellEditors().put("type", typeListButton);

        // Amount uses a TextInput with strict currency validation
        TextInput amountTextInput = new TextInput();
        amountTextInput.setValidator(new CurrencyValidator());
        amountTextInput.getStyles().put("strictValidation", true);
        amountTextInput.setTextKey("amount");
        tableViewRowEditor.getCellEditors().put("amount", amountTextInput);

        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) throws Exception {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    public static void main(String[] args) {
        DesktopApplicationContext.main(RowEditorDemo.class, args);
    }
}