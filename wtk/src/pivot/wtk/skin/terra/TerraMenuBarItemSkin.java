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
package pivot.wtk.skin.terra;

import java.awt.Color;
import java.awt.Graphics2D;

import pivot.wtk.Button;
import pivot.wtk.Component;
import pivot.wtk.Cursor;
import pivot.wtk.Dimensions;
import pivot.wtk.MenuBar;
import pivot.wtk.skin.MenuBarItemSkin;

/**
 * Terra menu bar item skin.
 *
 * @author gbrown
 */
public class TerraMenuBarItemSkin extends MenuBarItemSkin {
    @Override
    public void install(Component component) {
        super.install(component);

        MenuBar.Item menuBarItem = (MenuBar.Item)component;
        menuBarItem.setCursor(Cursor.DEFAULT);
    }

    public int getPreferredWidth(int height) {
        MenuBar.Item menuBarItem = (MenuBar.Item)getComponent();

        Button.DataRenderer dataRenderer = menuBarItem.getDataRenderer();
        dataRenderer.render(menuBarItem.getButtonData(), menuBarItem, false);

        return dataRenderer.getPreferredWidth(height);
    }

    public int getPreferredHeight(int width) {
        MenuBar.Item menuBarItem = (MenuBar.Item)getComponent();

        Button.DataRenderer dataRenderer = menuBarItem.getDataRenderer();
        dataRenderer.render(menuBarItem.getButtonData(), menuBarItem, false);

        return dataRenderer.getPreferredHeight(width);
    }

    public Dimensions getPreferredSize() {
        MenuBar.Item menuBarItem = (MenuBar.Item)getComponent();

        Button.DataRenderer dataRenderer = menuBarItem.getDataRenderer();
        dataRenderer.render(menuBarItem.getButtonData(), menuBarItem, false);

        return dataRenderer.getPreferredSize();
    }

    public void layout() {
        // No-op
    }

    public void paint(Graphics2D graphics) {
        MenuBar.Item menuBarItem = (MenuBar.Item)getComponent();
        MenuBar menuBar = menuBarItem.getMenuBar();

        int width = getWidth();
        int height = getHeight();

        boolean highlight = menuPopup.isOpen();

        // Paint highlight state
        if (highlight) {
            Color highlightBackgroundColor = (Color)menuBar.getStyles().get("highlightBackgroundColor");
            graphics.setColor(highlightBackgroundColor);
            graphics.fillRect(0, 0, width, height);
        }

        // Paint the content
        Button.DataRenderer dataRenderer = menuBarItem.getDataRenderer();
        dataRenderer.render(menuBarItem.getButtonData(), menuBarItem, highlight);
        dataRenderer.setSize(width, height);

        dataRenderer.paint(graphics);
    }

    public Color getPopupBorderColor() {
        return (Color)menuPopup.getStyles().get("borderColor");
    }

    public void setPopupBorderColor(Color popupBorderColor) {
        menuPopup.getStyles().put("borderColor", popupBorderColor);
    }

    public void setPopupBorderColor(String popupBorderColor) {
        if (popupBorderColor == null) {
            throw new IllegalArgumentException("popupBorderColor is null.");
        }

        menuPopup.getStyles().put("borderColor", popupBorderColor);
    }
}