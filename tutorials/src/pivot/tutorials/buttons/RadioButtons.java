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
package pivot.tutorials.buttons;

import pivot.collections.Dictionary;
import pivot.wtk.Alert;
import pivot.wtk.Application;
import pivot.wtk.Button;
import pivot.wtk.ButtonPressListener;
import pivot.wtk.DesktopApplicationContext;
import pivot.wtk.Display;
import pivot.wtk.MessageType;
import pivot.wtk.PushButton;
import pivot.wtk.RadioButton;
import pivot.wtk.Window;
import pivot.wtkx.Bindable;

public class RadioButtons extends Bindable implements Application {
    @Load(name="radio_buttons.wtkx") private Window window;
    @Bind(property="window") private RadioButton oneButton;
    @Bind(property="window") private PushButton selectButton;

    public void startup(Display display, Dictionary<String, String> properties)
        throws Exception {
        bind();

        // Get a reference to the button group
        final Button.Group numbersGroup = oneButton.getGroup();

        // Add a button press listener
        selectButton.getButtonPressListeners().add(new ButtonPressListener() {
            public void buttonPressed(Button button) {
                String message = "You selected \""
                    + numbersGroup.getSelection().getButtonData()
                    + "\".";
                Alert.alert(MessageType.INFO, message, window);
            }
        });

        window.open(display);
    }

    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return true;
    }

    public void suspend() {
    }

    public void resume() {
    }

    public static void main(String[] args) {
        DesktopApplicationContext.main(RadioButtons.class, args);
    }
}
