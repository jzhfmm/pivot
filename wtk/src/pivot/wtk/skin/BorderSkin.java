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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import pivot.collections.Dictionary;
import pivot.wtk.Border;
import pivot.wtk.BorderListener;
import pivot.wtk.Component;
import pivot.wtk.CornerRadii;
import pivot.wtk.Dimensions;
import pivot.wtk.Insets;
import pivot.wtk.Platform;
import pivot.wtk.Theme;

/**
 * Border skin.
 * <p>
 * TODO Draw title.
 *
 * @author gbrown
 */
public class BorderSkin extends ContainerSkin
    implements BorderListener {
    private FontRenderContext fontRenderContext = new FontRenderContext(null, true, true);

    private Font font;
    private Color color;
    private Color titleColor;
    private int thickness;
    private Insets padding;
    private CornerRadii cornerRadii;

    public BorderSkin() {
        Theme theme = Theme.getTheme();
        setBackgroundColor(Color.WHITE);

        font = theme.getFont().deriveFont(Font.BOLD);
        color = Color.BLACK;
        titleColor = Color.BLACK;
        thickness = 1;
        padding = new Insets(2);
        cornerRadii = new CornerRadii(0);
    }

    @Override
    public void install(Component component) {
        super.install(component);

        Border border = (Border)component;
        border.getBorderListeners().add(this);
    }

    @Override
    public void uninstall() {
        Border border = (Border)getComponent();
        border.getBorderListeners().remove(this);

        super.uninstall();
    }

    @Override
    public int getPreferredWidth(int height) {
        int preferredWidth = 0;

        Border border = (Border)getComponent();
        int topThickness = thickness;

        String title = border.getTitle();
        if (title != null
            && title.length() > 0) {
            Rectangle2D headingBounds = font.getStringBounds(title, fontRenderContext);
            preferredWidth = (int)Math.ceil(headingBounds.getWidth());

            LineMetrics lm = font.getLineMetrics(title, fontRenderContext);
            topThickness = Math.max((int)Math.ceil(lm.getAscent() + lm.getDescent()
                + lm.getLeading()), topThickness);
        }

        Component content = border.getContent();
        if (content != null
            && content.isDisplayable()) {
            if (height != -1) {
                height = Math.max(height - (topThickness + thickness) -
                    padding.top - padding.bottom, 0);
            }

            preferredWidth = content.getPreferredWidth(height);
        }

        preferredWidth += (padding.left + padding.right) + (thickness * 2);

        return preferredWidth;
    }

    @Override
    public int getPreferredHeight(int width) {
        int preferredHeight = 0;

        Border border = (Border)getComponent();
        int topThickness = thickness;

        String title = border.getTitle();
        if (title != null
            && title.length() > 0) {
            LineMetrics lm = font.getLineMetrics(title, fontRenderContext);
            topThickness = Math.max((int)Math.ceil(lm.getAscent() + lm.getDescent()
                + lm.getLeading()), topThickness);
        }

        Component content = border.getContent();
        if (content != null
            && content.isDisplayable()) {
            if (width != -1) {
                width = Math.max(width - (thickness * 2)
                    - padding.left - padding.right, 0);
            }

            preferredHeight = content.getPreferredHeight(width);
        }

        preferredHeight += (padding.top + padding.bottom) + (topThickness + thickness);

        return preferredHeight;
    }

    @Override
    public Dimensions getPreferredSize() {
        // TODO Optimize
        return new Dimensions(getPreferredWidth(-1), getPreferredHeight(-1));
    }

    public void layout() {
        int width = getWidth();
        int height = getHeight();

        Border border = (Border)getComponent();
        int topThickness = thickness;

        String title = border.getTitle();
        if (title != null
            && title.length() > 0) {
            LineMetrics lm = font.getLineMetrics(title, fontRenderContext);
            topThickness = Math.max((int)Math.ceil(lm.getAscent() + lm.getDescent()
                + lm.getLeading()), topThickness);
        }

        Component content = border.getContent();
        if (content != null) {
            if (content.isDisplayable()) {
                content.setVisible(true);

                content.setLocation(padding.left + thickness,
                    padding.top + topThickness);

                int contentWidth = Math.max(width - (padding.left + padding.right
                    + (thickness * 2)), 0);
                int contentHeight = Math.max(height - (padding.top + padding.bottom
                    + (topThickness + thickness)), 0);

                content.setSize(contentWidth, contentHeight);
            } else {
                content.setVisible(false);
            }
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        Border border = (Border)getComponent();
        int topThickness = thickness;
        float titleAscent = 0;

        String title = border.getTitle();
        if (title != null
            && title.length() > 0) {
            LineMetrics lm = font.getLineMetrics(title, fontRenderContext);
            titleAscent = lm.getAscent();
            topThickness = Math.max((int)Math.ceil(titleAscent
                + lm.getDescent() + lm.getLeading()), topThickness);
        }

        // TODO Java2D doesn't support variable corner radii; we'll need to
        // "fake" this by drawing multiple arcs
        int cornerRadius = cornerRadii.topLeft;

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        int x = thickness / 2;
        int y = topThickness / 2;
        int width = Math.max(getWidth() - thickness, 0);
        int height = Math.max(getHeight() - (int)Math.ceil((topThickness + thickness) * 0.5), 0);

        // Draw the background
        Color backgroundColor = getBackgroundColor();
        if (backgroundColor != null) {
            graphics.setPaint(backgroundColor);
            graphics.fillRoundRect(x, y, width, height, cornerRadius, cornerRadius);
        }

        // Draw the title
        if (title != null) {
            if (fontRenderContext.isAntiAliased()) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    Platform.getTextAntialiasingHint());
            }

            if (fontRenderContext.usesFractionalMetrics()) {
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            }

            // Note that we add one pixel to the string bounds for spacing
            Rectangle2D titleBounds = font.getStringBounds(title, fontRenderContext);
            titleBounds = new Rectangle2D.Double((double)(padding.left + thickness),
                (topThickness - titleBounds.getHeight()) / 2,
                    titleBounds.getWidth() + 1, titleBounds.getHeight());

            graphics.setFont(font);
            graphics.setPaint(titleColor);
            graphics.drawString(title, (int)titleBounds.getX(),
                (int)(titleBounds.getY() + titleAscent));

            Area titleClip = new Area(graphics.getClip());
            titleClip.subtract(new Area(titleBounds));
            graphics.clip(titleClip);
        }

        // Draw the border
        if (thickness > 0) {
            graphics.setPaint(color);
            graphics.setStroke(new BasicStroke(thickness));
            graphics.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius);
        }
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

        setColor(decodeColor(color));
    }

    public Color getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(Color titleColor) {
        if (titleColor == null) {
            throw new IllegalArgumentException("titleColor is null.");
        }

        this.titleColor = titleColor;
        repaintComponent();
    }

    public final void setTitleColor(String titleColor) {
        if (titleColor == null) {
            throw new IllegalArgumentException("titleColor is null.");
        }

        setTitleColor(decodeColor(titleColor));
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        repaintComponent();
    }

    public void setThickness(Number thickness) {
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

    public void setPadding(Number padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(padding.intValue());
    }

    public CornerRadii getCornerRadii() {
        return cornerRadii;
    }

    public void setCornerRadii(CornerRadii cornerRadii) {
        if (cornerRadii == null) {
            throw new IllegalArgumentException("cornerRadii is null.");
        }

        this.cornerRadii = cornerRadii;
        repaintComponent();
    }

    public final void setCornerRadii(Dictionary<String, ?> cornerRadii) {
        if (cornerRadii == null) {
            throw new IllegalArgumentException("cornerRadii is null.");
        }

        setCornerRadii(new CornerRadii(cornerRadii));
    }

    public final void setCornerRadii(int cornerRadii) {
        setCornerRadii(new CornerRadii(cornerRadii));
    }

    public void setCornerRadii(Number cornerRadii) {
        if (cornerRadii == null) {
            throw new IllegalArgumentException("cornerRadii is null.");
        }

        setCornerRadii(cornerRadii.intValue());
    }

    // Border events
    public void titleChanged(Border border, String previousTitle) {
        invalidateComponent();
    }

    public void contentChanged(Border border, Component previousContent) {
        invalidateComponent();
    }
}