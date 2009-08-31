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
package org.apache.pivot.wtk.skin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.Insets;
import org.apache.pivot.wtk.Platform;
import org.apache.pivot.wtk.Separator;
import org.apache.pivot.wtk.SeparatorListener;
import org.apache.pivot.wtk.Theme;


/**
 * Separator skin.
 */
public class SeparatorSkin extends ComponentSkin
    implements SeparatorListener {
    private FontRenderContext fontRenderContext = new FontRenderContext(null, true, true);

    private Font font;
    private Color color;
    private Color headingColor;
    private int thickness;
    private Insets padding;

    public SeparatorSkin() {
        Theme theme = Theme.getTheme();
        font = theme.getFont().deriveFont(Font.BOLD);
        color = Color.BLACK;
        headingColor = Color.BLACK;
        thickness = 1;
        padding = new Insets(4, 0, 4, 4);
    }

    @Override
    public void install(Component component) {
        super.install(component);

        Separator separator = (Separator)component;
        separator.getSeparatorListeners().add(this);
    }

    @Override
    public void uninstall() {
        Separator separator = (Separator)getComponent();
        separator.getSeparatorListeners().remove(this);

        super.uninstall();
    }

    @Override
    public int getPreferredWidth(int height) {
        int preferredWidth = 0;

        Separator separator = (Separator)getComponent();
        String heading = separator.getHeading();

        if (heading != null
            && heading.length() > 0) {
            Rectangle2D headingBounds = font.getStringBounds(heading, fontRenderContext);
            preferredWidth = (int)Math.ceil(headingBounds.getWidth())
                + (padding.left + padding.right);
        }

        return preferredWidth;
    }

    @Override
    public int getPreferredHeight(int width) {
        int preferredHeight = thickness;

        Separator separator = (Separator)getComponent();
        String heading = separator.getHeading();

        if (heading != null
            && heading.length() > 0) {
            LineMetrics lm = font.getLineMetrics(heading, fontRenderContext);
            preferredHeight = Math.max((int)Math.ceil(lm.getAscent() + lm.getDescent()
                + lm.getLeading()), preferredHeight);
        }

        preferredHeight += (padding.top + padding.bottom);

        return preferredHeight;
    }

    @Override
    public Dimensions getPreferredSize() {
        // TODO Optimize
        return new Dimensions(getPreferredWidth(-1), getPreferredHeight(-1));
    }

    @Override
    public void layout() {
        // No-op
    }

    @Override
    public void paint(Graphics2D graphics) {
        Separator separator = (Separator)getComponent();
        int width = getWidth();
        int separatorY = padding.top;

        String heading = separator.getHeading();

        if (heading != null
            && heading.length() > 0) {
            LineMetrics lm = font.getLineMetrics(heading, fontRenderContext);

            if (fontRenderContext.isAntiAliased()) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    Platform.getTextAntialiasingHint());
            }

            if (fontRenderContext.usesFractionalMetrics()) {
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            }

            graphics.setFont(font);
            graphics.setPaint(headingColor);
            graphics.drawString(heading, padding.left, lm.getAscent() + padding.top);

            Rectangle2D headingBounds = font.getStringBounds(heading, fontRenderContext);

            Area titleClip = new Area(graphics.getClip());
            titleClip.subtract(new Area(new Rectangle2D.Double(padding.left, padding.top,
                headingBounds.getWidth() + padding.right, headingBounds.getHeight())));
            graphics.clip(titleClip);

            separatorY += (lm.getAscent() + lm.getDescent()) / 2 + 1;
        }

        graphics.setStroke(new BasicStroke(thickness));
        graphics.setColor(color);
        graphics.drawLine(0, separatorY, width, separatorY);
    }

    /**
     * @return
     * <tt>false</tt>; spacers are not focusable.
     */
    @Override
    public boolean isFocusable() {
        return false;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("font is null.");
        }

        this.font = font;
        invalidateComponent();
    }

    public final void setFont(String font) {
        if (font == null) {
            throw new IllegalArgumentException("font is null.");
        }

        setFont(Font.decode(font));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color is null.");
        }

        this.color = color;
        repaintComponent();
    }

    public final void setColor(String color) {
        if (color == null) {
            throw new IllegalArgumentException("color is null.");
        }

        setColor(GraphicsUtilities.decodeColor(color));
    }

    public Color getHeadingColor() {
        return headingColor;
    }

    public void setHeadingColor(Color headingColor) {
        if (headingColor == null) {
            throw new IllegalArgumentException("headingColor is null.");
        }

        this.headingColor = headingColor;
        repaintComponent();
    }

    public final void setHeadingColor(String headingColor) {
        if (headingColor == null) {
            throw new IllegalArgumentException("headingColor is null.");
        }

        setHeadingColor(GraphicsUtilities.decodeColor(headingColor));
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        invalidateComponent();
    }

    public final void setThickness(Number thickness) {
        if (thickness == null) {
            throw new IllegalArgumentException("thickness is null.");
        }

        setThickness(thickness.intValue());
    }

    public Insets getPadding() {
        return padding;
    }

    public void setPadding(Insets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        this.padding = padding;
        invalidateComponent();
    }

    public final void setPadding(Dictionary<String, ?> padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(new Insets(padding));
    }

    public final void setPadding(int padding) {
        setPadding(new Insets(padding));
    }

    public final void setPadding(Number padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(padding.intValue());
    }

    public final void setPadding(String padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(Insets.decode(padding));
    }

    // Separator events
    @Override
    public void headingChanged(Separator separator, String previousHeading) {
        invalidateComponent();
    }
}
