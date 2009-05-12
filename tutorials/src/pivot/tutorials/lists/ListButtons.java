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
package pivot.tutorials.lists;

import java.net.URL;
import pivot.collections.Dictionary;
import pivot.util.ThreadUtilities;
import pivot.wtk.Application;
import pivot.wtk.ApplicationContext;
import pivot.wtk.DesktopApplicationContext;
import pivot.wtk.Display;
import pivot.wtk.ImageView;
import pivot.wtk.ListButton;
import pivot.wtk.ListButtonSelectionListener;
import pivot.wtk.Window;
import pivot.wtk.media.Image;
import pivot.wtkx.Bindable;

public class ListButtons extends Bindable implements Application {
    @Load(name="list_buttons.wtkx") private Window window;
    @Bind(property="window") private ListButton listButton;
    @Bind(property="window") private ImageView imageView;

    private ListButtonSelectionListener listButtonSelectionListener =
        new ListButtonSelectionListener() {
        public void selectedIndexChanged(ListButton listButton, int previousIndex) {
            int index = listButton.getSelectedIndex();

            if (index != -1) {
                String item = (String)listButton.getListData().get(index);

                // Get the image URL for the selected item
                ClassLoader classLoader = ThreadUtilities.getClassLoader();
                URL imageURL = classLoader.getResource("pivot/tutorials/" + item);

                // If the image has not been added to the resource cache yet,
                // add it
                Image image = (Image)ApplicationContext.getResourceCache().get(imageURL);

                if (image == null) {
                    image = Image.load(imageURL);
                    ApplicationContext.getResourceCache().put(imageURL, image);
                }

                // Update the image
                imageView.setImage(image);
            }
        }
    };

    public void startup(Display display, Dictionary<String, String> properties) throws Exception {
        bind();

        listButton.getListButtonSelectionListeners().add(listButtonSelectionListener);
        listButton.setSelectedIndex(0);

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
        DesktopApplicationContext.main(ListButtons.class, args);
    }
}
