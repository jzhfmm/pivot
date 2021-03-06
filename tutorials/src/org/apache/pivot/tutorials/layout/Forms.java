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
package org.apache.pivot.tutorials.layout;

import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtkx.WTKXSerializer;

public class Forms implements Application {
    private Window window = null;
    private BoxPane nameBoxPane = null;
    private TextInput lastNameTextInput = null;
    private TextInput firstNameTextInput = null;
    private PushButton submitButton = null;
    private Label errorLabel = null;

    @Override
    public void startup(Display display, Map<String, String> properties) throws Exception {
        WTKXSerializer wtkxSerializer = new WTKXSerializer();
        window = (Window)wtkxSerializer.readObject(this, "forms.wtkx");
        nameBoxPane = (BoxPane)wtkxSerializer.get("nameBoxPane");
        lastNameTextInput = (TextInput)wtkxSerializer.get("lastNameTextInput");
        firstNameTextInput = (TextInput)wtkxSerializer.get("firstNameTextInput");
        submitButton = (PushButton)wtkxSerializer.get("submitButton");
        errorLabel = (Label)wtkxSerializer.get("errorLabel");

        submitButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                String lastName = lastNameTextInput.getText();
                String firstName = firstNameTextInput.getText();

                Form.Flag flag = null;
                if (lastName.length() == 0
                    || firstName.length() == 0) {
                    flag = new Form.Flag(MessageType.ERROR, "Name is required.");
                }

                Form.setFlag(nameBoxPane, flag);

                if (flag == null) {
                    errorLabel.setText(null);
                    Prompt.prompt("Pretending to submit...", window);
                } else {
                    errorLabel.setText("Some required information is missing.");
                }
            }
        });

        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
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
        DesktopApplicationContext.main(Forms.class, args);
    }
}
