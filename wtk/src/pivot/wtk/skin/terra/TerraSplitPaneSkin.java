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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import pivot.wtk.Component;
import pivot.wtk.Cursor;
import pivot.wtk.Dimensions;
import pivot.wtk.Mouse;
import pivot.wtk.Orientation;
import pivot.wtk.Span;
import pivot.wtk.SplitPane;
import pivot.wtk.SplitPaneListener;
import pivot.wtk.Theme;
import pivot.wtk.skin.ComponentSkin;
import pivot.wtk.skin.ContainerSkin;

/**
 * Split pane skin.
 *
 * @author tvolkert
 */
public class TerraSplitPaneSkin extends ContainerSkin
    implements SplitPaneListener {

    /**
     * Split pane splitter component.
     *
     * @author tvolkert
     */
    protected class Splitter extends Component {
        public Splitter() {
            setSkin(new SplitterSkin());
        }
    }

    /**
     * Split pane splitter component skin.
     *
     * @author tvolkert
     */
    protected class SplitterSkin extends ComponentSkin {
        private int dragOffset;
        private SplitterShadow shadow = null;

        @Override
        public boolean isFocusable() {
            return false;
        }

        public int getPreferredWidth(int height) {
            // This will never get called since the size of the splitter is set
            // automatically by SplitPaneSkin using the the size of the
            // SplitPane and the split thickness
            return 0;
        }

        public int getPreferredHeight(int width) {
            // This will never get called since the size of the splitter is set
            // automatically by SplitPaneSkin using the the size of the
            // SplitPane and the split thickness
            return 0;
        }

        public void layout() {
            // No-op
        }

        public void paint(Graphics2D graphics) {
            SplitPane splitPane = (SplitPane)TerraSplitPaneSkin.this.getComponent();

            Orientation orientation = splitPane.getOrientation();

            int width = getWidth();
            int height = getHeight();

            int imageWidth, imageHeight;
            if (orientation == Orientation.HORIZONTAL) {
                imageWidth = width - 4;
                imageHeight = Math.min(height - 4, 8);
            } else {
                imageWidth = Math.min(width - 4, 8);
                imageHeight = height - 4;
            }

            if (imageWidth > 0 && imageHeight > 0) {
                int translateX = (width - imageWidth) / 2;
                int translateY = (height - imageHeight) / 2;
                graphics.translate(translateX, translateY);

                Color dark = splitterHandlePrimaryColor;
                Color light = splitterHandleSecondaryColor;

                if (orientation == Orientation.HORIZONTAL) {
                    graphics.setStroke(new BasicStroke());

                    graphics.setPaint(dark);
                    graphics.drawLine(0, 0, imageWidth - 1, 0);
                    graphics.drawLine(0, 3, imageWidth - 1, 3);
                    graphics.drawLine(0, 6, imageWidth - 1, 6);

                    graphics.setPaint(light);
                    graphics.drawLine(0, 1, imageWidth - 1, 1);
                    graphics.drawLine(0, 4, imageWidth - 1, 4);
                    graphics.drawLine(0, 7, imageWidth - 1, 7);
                } else {
                    int half = imageHeight / 2;

                    graphics.setPaint(dark);
                    graphics.fillRect(0, 0, 2, half);
                    graphics.fillRect(3, 0, 2, half);
                    graphics.fillRect(6, 0, 2, half);

                    graphics.setPaint(light);
                    graphics.fillRect(0, half, 2, half);
                    graphics.fillRect(3, half, 2, half);
                    graphics.fillRect(6, half, 2, half);
                }
            }
        }

        @Override
        public boolean mouseMove(Component component, int x, int y) {
            boolean consumed = super.mouseMove(component, x, y);

            if (Mouse.getCapturer() == component) {
                SplitPane splitPane = (SplitPane)TerraSplitPaneSkin.this.getComponent();
                Orientation orientation = splitPane.getOrientation();

                // Calculate the would-be new split location
                int splitLocation;
                if (orientation == Orientation.HORIZONTAL) {
                    splitLocation = component.getX() + x - dragOffset;
                } else {
                    splitLocation = component.getY() + y - dragOffset;
                }

                // Bound the split location
                splitLocation = boundSplitLocation(splitLocation);

                if (shadow == null) {
                    // Update the split location immediately
                    splitPane.setSplitLocation(splitLocation);
                } else {
                    // Move the shadow to the split location
                    if (orientation == Orientation.HORIZONTAL) {
                        shadow.setLocation(splitLocation, 0);
                    } else {
                        shadow.setLocation(0, splitLocation);
                    }
                }
            }

            return consumed;
        }

        @Override
        public boolean mouseDown(Component component, Mouse.Button button, int x, int y) {
            boolean consumed = super.mouseDown(component, button, x, y);

            SplitPane splitPane = (SplitPane)TerraSplitPaneSkin.this.getComponent();

            if (button == Mouse.Button.LEFT
                && !splitPane.isLocked()) {
                Orientation orientation = splitPane.getOrientation();

                if (useShadow) {
                    // Add the shadow to the split pane and lay it out
                    shadow = new SplitterShadow();
                    splitPane.add(shadow);

                    if (orientation == Orientation.HORIZONTAL) {
                        shadow.setLocation(component.getX(), 0);
                    } else {
                        shadow.setLocation(0, component.getY());
                    }

                    shadow.setSize(getWidth(), getHeight());
                }

                dragOffset = (orientation == Orientation.HORIZONTAL ? x : y);
                Mouse.capture(component);
                consumed = true;
            }

            return consumed;
        }

        @Override
        public boolean mouseUp(Component component, Mouse.Button button, int x, int y) {
            boolean consumed = super.mouseUp(component, button, x, y);

            if (button == Mouse.Button.LEFT
                && Mouse.getCapturer() == component) {
                if (shadow != null) {
                    SplitPane splitPane = (SplitPane)TerraSplitPaneSkin.this.getComponent();

                    // Update the split location and remove the shadow
                    int splitLocation;
                    if (splitPane.getOrientation() == Orientation.HORIZONTAL) {
                        splitLocation = shadow.getX();
                    } else {
                        splitLocation = shadow.getY();
                    }

                    splitPane.setSplitLocation(splitLocation);

                    splitPane.remove(shadow);
                    shadow = null;
                }

                Mouse.release();
            }

            return consumed;
        }

        private int boundSplitLocation(int splitLocation) {
            SplitPane splitPane = (SplitPane)TerraSplitPaneSkin.this.getComponent();

            int lower = 0;
            int upper;
            if (splitPane.getOrientation() == Orientation.HORIZONTAL) {
                upper = splitPane.getWidth() - splitterThickness;
            } else {
                upper = splitPane.getHeight() - splitterThickness;
            }

            Span bounds = splitPane.getSplitBounds();
            if (bounds != null) {
                lower = Math.max(lower, bounds.getStart());
                upper = Math.min(upper, bounds.getEnd());
            }

            if (splitLocation < lower) {
                splitLocation = lower;
            } else if (splitLocation > upper) {
                splitLocation = upper;
            }

            return splitLocation;
        }
    }

    /**
     * Split pane splitter shadow component.
     *
     * @author tvolkert
     */
    protected class SplitterShadow extends Component {
        public SplitterShadow() {
            super();
            setSkin(new SplitterShadowSkin());
        }
    }

    /**
     * Split pane splitter shadow component skin.
     *
     * @author tvolkert
     */
    protected class SplitterShadowSkin extends ComponentSkin {
        public int getPreferredWidth(int height) {
            // This will never get called since the splitter will always just
            // set the size of its shadow to match its own size
            return 0;
        }

        public int getPreferredHeight(int width) {
            // This will never get called since the splitter will always just
            // set the size of its shadow to match its own size
            return 0;
        }

        public void layout() {
            // No-op
        }

        public void paint(Graphics2D graphics) {
            graphics.setStroke(new BasicStroke());
            graphics.setPaint(new Color(0, 0, 0, 64));
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private Splitter splitter = new Splitter();

    private Color splitterHandlePrimaryColor;
    private Color splitterHandleSecondaryColor;
    private int splitterThickness;
    private boolean useShadow;

    public TerraSplitPaneSkin() {
        TerraTheme theme = (TerraTheme)Theme.getTheme();
        splitterHandlePrimaryColor = theme.getColor(9);
        splitterHandleSecondaryColor = theme.getColor(10);
        splitterThickness = 6;
        useShadow = false;
    }

    @Override
    public void install(Component component) {
        super.install(component);

        SplitPane splitPane = (SplitPane)component;
        splitPane.getSplitPaneListeners().add(this);

        splitPane.add(splitter);
        updateSplitterCursor();
    }

    @Override
    public void uninstall() {
        SplitPane splitPane = (SplitPane)getComponent();
        splitPane.getSplitPaneListeners().remove(this);

        splitPane.remove(splitter);

        super.uninstall();
    }

    @Override
    public int getPreferredWidth(int height) {
        return 0;
    }

    @Override
    public int getPreferredHeight(int width) {
        return 0;
    }

    @Override
    public Dimensions getPreferredSize() {
        return new Dimensions(0, 0);
    }

    public void layout() {
        int width = getWidth();
        int height = getHeight();

        SplitPane splitPane = (SplitPane)getComponent();
        int splitLocation = splitPane.getSplitLocation();
        Component leftComponent = splitPane.getTopLeftComponent();
        Component rightComponent = splitPane.getBottomRightComponent();

        int rightStart = splitLocation + splitterThickness;

        if (splitPane.getOrientation() == Orientation.HORIZONTAL) {
            splitter.setLocation(splitLocation, 0);
            splitter.setSize(splitterThickness, height);

            if (leftComponent != null) {
                leftComponent.setLocation(0, 0);
                leftComponent.setSize(splitLocation, height);
            }

            if (rightComponent != null) {
                rightComponent.setLocation(rightStart, 0);
                rightComponent.setSize(Math.max(width - rightStart, 0), height);
            }
        } else {
            splitter.setLocation(0, splitLocation);
            splitter.setSize(width, splitterThickness);

            if (leftComponent != null) {
                leftComponent.setLocation(0, 0);
                leftComponent.setSize(width, splitLocation);
            }

            if (rightComponent != null) {
                rightComponent.setLocation(0, rightStart);
                rightComponent.setSize(width, Math.max(height - rightStart, 0));
            }
        }
    }

    public Color getSplitterHandlePrimaryColor() {
        return splitterHandlePrimaryColor;
    }

    public void setSplitterHandlePrimaryColor(Color splitterHandlePrimaryColor) {
        if (splitterHandlePrimaryColor == null) {
            throw new IllegalArgumentException("splitterHandlePrimaryColor is null.");
        }

        this.splitterHandlePrimaryColor = splitterHandlePrimaryColor;
        splitter.repaint();
    }

    public final void setSplitterHandlePrimaryColor(String splitterHandlePrimaryColor) {
        if (splitterHandlePrimaryColor == null) {
            throw new IllegalArgumentException("splitterHandlePrimaryColor is null.");
        }

        setSplitterHandlePrimaryColor(decodeColor(splitterHandlePrimaryColor));
    }

    public final void setSplitterHandlePrimaryColor(int splitterHandlePrimaryColor) {
        TerraTheme theme = (TerraTheme)Theme.getTheme();
        setSplitterHandlePrimaryColor(theme.getColor(splitterHandlePrimaryColor));
    }

    public Color getSplitterHandleSecondaryColor() {
        return splitterHandleSecondaryColor;
    }

    public void setSplitterHandleSecondaryColor(Color splitterHandleSecondaryColor) {
        if (splitterHandleSecondaryColor == null) {
            throw new IllegalArgumentException("splitterHandleSecondaryColor is null.");
        }

        this.splitterHandleSecondaryColor = splitterHandleSecondaryColor;
        splitter.repaint();
    }

    public final void setSplitterHandleSecondaryColor(String splitterHandleSecondaryColor) {
        if (splitterHandleSecondaryColor == null) {
            throw new IllegalArgumentException("splitterHandleSecondaryColor is null.");
        }

        setSplitterHandleSecondaryColor(decodeColor(splitterHandleSecondaryColor));
    }

    public final void setSplitterHandleSecondaryColor(int splitterHandleSecondaryColor) {
        TerraTheme theme = (TerraTheme)Theme.getTheme();
        setSplitterHandleSecondaryColor(theme.getColor(splitterHandleSecondaryColor));
    }

    public int getSplitterThickness() {
        return splitterThickness;
    }

    public void setSplitterThickness(int splitterThickness) {
        this.splitterThickness = splitterThickness;
        invalidateComponent();
    }

    public boolean getUseShadow() {
        return useShadow;
    }

    public void setUseShadow(boolean useShadow) {
        this.useShadow = useShadow;
    }

    public void topLeftComponentChanged(SplitPane splitPane, Component previousTopLeftComponent) {
        invalidateComponent();
    }

    public void bottomRightComponentChanged(SplitPane splitPane, Component previousBottomRightComponent) {
        invalidateComponent();
    }

    public void orientationChanged(SplitPane splitPane) {
        updateSplitterCursor();
        invalidateComponent();
    }

    public void primaryRegionChanged(SplitPane splitPane) {
        updateSplitterCursor();
    }

    public void splitLocationChanged(SplitPane splitPane, int previousSplitLocation) {
        invalidateComponent();
    }

    public void splitBoundsChanged(SplitPane splitPane, Span previousSplitBounds) {
        invalidateComponent();
    }

    public void lockedChanged(SplitPane splitPane) {
        updateSplitterCursor();
    }

    private void updateSplitterCursor() {
        Cursor cursor = Cursor.DEFAULT;
        SplitPane splitPane = (SplitPane)getComponent();

        if (!splitPane.isLocked()) {
            switch (splitPane.getOrientation()) {
            case HORIZONTAL:
                switch (splitPane.getPrimaryRegion()) {
                case TOP_LEFT:
                    cursor = Cursor.RESIZE_EAST;
                    break;
                case BOTTOM_RIGHT:
                    cursor = Cursor.RESIZE_WEST;
                    break;
                }
                break;

            case VERTICAL:
                switch (splitPane.getPrimaryRegion()) {
                case TOP_LEFT:
                    cursor = Cursor.RESIZE_SOUTH;
                    break;
                case BOTTOM_RIGHT:
                    cursor = Cursor.RESIZE_NORTH;
                    break;
                }
                break;
            }
        }

        splitter.setCursor(cursor);
    }
}