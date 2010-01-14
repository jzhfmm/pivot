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
package pivot.wtk.skin;

import pivot.util.Vote;
import pivot.wtk.ApplicationContext;
import pivot.wtk.Button;
import pivot.wtk.Component;
import pivot.wtk.Container;
import pivot.wtk.ContainerMouseListener;
import pivot.wtk.Direction;
import pivot.wtk.Display;
import pivot.wtk.Keyboard;
import pivot.wtk.Menu;
import pivot.wtk.MenuPopup;
import pivot.wtk.Mouse;
import pivot.wtk.Point;
import pivot.wtk.Window;
import pivot.wtk.WindowStateListener;

/**
 * Abstract base class for menu item skins.
 *
 * @author gbrown
 */
public abstract class MenuItemSkin extends ButtonSkin implements Menu.ItemListener {
    protected MenuPopup menuPopup = new MenuPopup();

    protected int buttonPressInterval = 200;
    protected ApplicationContext.ScheduledCallback buttonPressCallback = null;

    private WindowStateListener menuPopupWindowListener = new WindowStateListener() {
        public Vote previewWindowOpen(Window window, Display display) {
            return Vote.APPROVE;
        }

        public void windowOpenVetoed(Window window, Vote reason) {
            // No-op
        }

        public void windowOpened(Window window) {
            Display display = window.getDisplay();
            display.getContainerMouseListeners().add(displayMouseListener);
        }

        public Vote previewWindowClose(Window window) {
            return Vote.APPROVE;
        }

        public void windowCloseVetoed(Window window, Vote reason) {
            // No-op
        }

        public void windowClosed(Window window, Display display) {
            display.getContainerMouseListeners().remove(displayMouseListener);

            Menu.Item menuItem = (Menu.Item)getComponent();
            if (menuItem.isFocused()) {
                Component.clearFocus();
            } else {
                repaintComponent();
            }
        }
    };

    private ContainerMouseListener displayMouseListener = new ContainerMouseListener() {
        public boolean mouseMove(Container container, int x, int y) {
            return false;
        }

        public boolean mouseDown(Container container, Mouse.Button button, int x, int y) {
            Display display = (Display)container;
            Component descendant = display.getDescendantAt(x, y);
            Window window = descendant.getWindow();

            if (!menuPopup.isAncestor(descendant)
                && (window == null
                    || !menuPopup.isOwner(window))
                && descendant != MenuItemSkin.this.getComponent()) {
                menuPopup.close();
            }

            return false;
        }

        public boolean mouseUp(Container container, Mouse.Button button, int x, int y) {
            return false;
        }

        public boolean mouseWheel(Container container, Mouse.ScrollType scrollType,
            int scrollAmount, int wheelRotation, int x, int y) {
            boolean consumed = false;

            Display display = (Display)container;
            Window window = (Window)display.getComponentAt(x, y);

            if (window != menuPopup
                && (window == null
                    || !menuPopup.isOwner(window))) {
                consumed = true;
            }

            return consumed;
        }
    };

    public MenuItemSkin() {
        menuPopup.getWindowStateListeners().add(menuPopupWindowListener);
    }

    @Override
    public void install(Component component) {
        super.install(component);

        Menu.Item menuItem = (Menu.Item)component;
        menuItem.getItemListeners().add(this);

        menuPopup.setMenu(menuItem.getMenu());
    }

    @Override
    public void uninstall() {
        Menu.Item menuItem = (Menu.Item)getComponent();
        menuItem.getItemListeners().remove(this);

        menuPopup.close();
        menuPopup.setMenu(null);

        super.uninstall();
    }

    @Override
    public void mouseOver(Component component) {
        super.mouseOver(component);

        if (buttonPressCallback != null) {
            buttonPressCallback.cancel();
            buttonPressCallback = null;
        }

        final Menu.Item menuItem = (Menu.Item)getComponent();
        if (menuItem.getMenu() != null) {
            buttonPressCallback = ApplicationContext.scheduleCallback(new Runnable() {
                public void run() {
                    menuItem.press();
                }
            }, buttonPressInterval);
        }

        menuItem.requestFocus();
    }

    @Override
    public void mouseOut(Component component) {
        super.mouseOut(component);

        if (buttonPressCallback != null) {
            buttonPressCallback.cancel();
            buttonPressCallback = null;
        }
    }

    @Override
    public boolean mouseDown(Component component, Mouse.Button button, int x, int y) {
        boolean consumed = super.mouseDown(component, button, x, y);

        if (buttonPressCallback != null) {
            buttonPressCallback.cancel();
            buttonPressCallback = null;
        }

        return consumed;
    }

    @Override
    public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
        boolean consumed = super.mouseClick(component, button, x, y, count);

        Menu.Item menuItem = (Menu.Item)getComponent();
        menuItem.press();

        return consumed;
    }

    @Override
    public boolean keyPressed(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
        boolean consumed = false;

        if (buttonPressCallback != null) {
            buttonPressCallback.cancel();
            buttonPressCallback = null;
        }

        Menu.Item menuItem = (Menu.Item)getComponent();
        Menu menu = menuItem.getMenu();

        if (keyCode == Keyboard.KeyCode.UP) {
            menuItem.transferFocus(Direction.BACKWARD);
            consumed = true;
        } else if (keyCode == Keyboard.KeyCode.DOWN) {
            menuItem.transferFocus(Direction.FORWARD);
            consumed = true;
        } else if (keyCode == Keyboard.KeyCode.LEFT) {
            Menu parentMenu = menuItem.getSection().getMenu();
            Menu.Item parentMenuItem = parentMenu.getItem();
            if (parentMenuItem != null) {
                parentMenuItem.requestFocus();
                consumed = true;
            }

            menuPopup.close();
        } else if (keyCode == Keyboard.KeyCode.RIGHT) {
            if (menu != null) {
                if (!menuPopup.isOpen()) {
                    menuItem.press();
                }

                menu.requestFocus();
                consumed = true;
            }
        } else if (keyCode == Keyboard.KeyCode.ENTER) {
            menuItem.press();
            consumed = true;
        } else if (keyCode == Keyboard.KeyCode.TAB) {
            // No-op
        } else {
            consumed = super.keyPressed(component, keyCode, keyLocation);
        }

        return consumed;
    }

    @Override
    public boolean keyReleased(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
        boolean consumed = false;

        Menu.Item menuItem = (Menu.Item)getComponent();

        if (keyCode == Keyboard.KeyCode.SPACE) {
            menuItem.press();
            consumed = true;
        } else {
            consumed = super.keyReleased(component, keyCode, keyLocation);
        }

        return consumed;
    }

    @Override
    public void enabledChanged(Component component) {
        super.enabledChanged(component);

        menuPopup.close();
    }

    @Override
    public void buttonPressed(Button button) {
        Menu.Item menuItem = (Menu.Item)getComponent();
        Menu menu = menuItem.getMenu();

        if (menu != null
            && !menuPopup.isOpen()) {
            // Determine the popup's location and preferred size, relative
            // to the menu item
            Display display = menuItem.getDisplay();
            Point menuItemLocation = menuItem.mapPointToAncestor(display, getWidth(), 0);

            // TODO Ensure that the popup remains within the bounds of the display

            menuPopup.setLocation(menuItemLocation.x, menuItemLocation.y);
            menuPopup.open(menuItem);
        }
    }

    public void menuChanged(Menu.Item menuItem, Menu previousMenu) {
        menuPopup.setMenu(menuItem.getMenu());
        repaintComponent();
    }
}