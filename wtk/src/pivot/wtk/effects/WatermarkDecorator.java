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
package pivot.wtk.effects;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.net.URL;

import pivot.wtk.Component;
import pivot.wtk.FlowPane;
import pivot.wtk.ImageView;
import pivot.wtk.Label;
import pivot.wtk.Orientation;
import pivot.wtk.Bounds;
import pivot.wtk.VerticalAlignment;
import pivot.wtk.media.Image;

/**
 * Decorator that paints a watermark effect over a component.
 *
 * @author tvolkert
 */
public class WatermarkDecorator implements Decorator {
    private float opacity = 0.075f;
    private double theta = Math.PI / 4;

    private FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
    private ImageView imageView = new ImageView();
    private Label label = new Label();

    private Component component = null;
    private Graphics2D graphics = null;

    /**
     * Cretes a new <tt>WatermarkDecorator</tt> with no text or image.
     */
    public WatermarkDecorator() {
        this(null, null);
    }

    /**
     * Cretes a new <tt>WatermarkDecorator</tt> with the specified string as
     * its text and no image.
     *
     * @param text
     * The text to paint over the decorated component
     */
    public WatermarkDecorator(String text) {
        this(text, null);
    }

    /**
     * Cretes a new <tt>WatermarkDecorator</tt> with no text and the specified
     * image.
     *
     * @param image
     * The image to paint over the decorated component
     */
    public WatermarkDecorator(Image image) {
        this(null, image);
    }

    /**
     * Cretes a new <tt>WatermarkDecorator</tt> with the specified text and
     * image.
     *
     * @param text
     * The text to paint over the decorated component
     *
     * @param image
     * The image to paint over the decorated component
     */
    public WatermarkDecorator(String text, Image image) {
        flowPane.add(imageView);
        flowPane.add(label);

        flowPane.getStyles().put("verticalAlignment", VerticalAlignment.CENTER);
        imageView.getStyles().put("opacity", opacity);

        Font font = (Font)label.getStyles().get("font");
        label.getStyles().put("font", font.deriveFont(Font.BOLD, 60));

        label.setText(text);
        imageView.setImage(image);

        validate();
    }

    /**
     * Gets the text that will be painted over this decorator's component.
     *
     * @return
     * This decorator's text
     */
    public String getText() {
        return label.getText();
    }

    /**
     * Sets the text that will be painted over this decorator's component.
     *
     * @param text
     * This decorator's text
     */
    public void setText(String text) {
        label.setText(text);
        validate();
    }

    /**
     * Gets the font that will be used when painting this decorator's text.
     *
     * @return
     * This decorator's font
     */
    public Font getFont() {
        return (Font)label.getStyles().get("font");
    }

    /**
     * Sets the font that will be used when painting this decorator's text.
     *
     * @param font
     * This decorator's font
     */
    public void setFont(Font font) {
        label.getStyles().put("font", font);
        validate();
    }

    /**
     * Sets the font that will be used when painting this decorator's text.
     *
     * @param font
     * This decorator's font
     */
    public final void setFont(String font) {
        setFont(Font.decode(font));
    }

    /**
     * Gets the image that will be painted over this decorator's component.
     *
     * @return
     * This decorator's image
     */
    public Image getImage() {
        return imageView.getImage();
    }

    /**
     * Sets the image that will be painted over this decorator's component.
     *
     * @param image
     * This decorator's image
     */
    public void setImage(Image image) {
        imageView.setImage(image);
        validate();
    }

    /**
     * Sets the image that will be painted over this decorator's component.
     *
     * @param image
     * This decorator's image
     */
    public void setImage(URL image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null.");
        }

        setImage(Image.load(image));
    }

    /**
     * Sets the image that will be painted over this decorator's component.
     *
     * @param image
     * This decorator's image
     */
    public void setImage(String image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null.");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        setImage(classLoader.getResource(image));
    }

    /**
     * Gets the opacity of the watermark.
     *
     * @return
     * This decorator's opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Sets the opacity of the watermark.
     *
     * @param opacity
     * This decorator's opacity
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
        imageView.getStyles().put("opacity", opacity);
    }

    /**
     * Gets the angle at the watermark will be painted, in radians.
     *
     * @return
     * This decorator's watermark angle
     */
    public double getTheta() {
        return theta;
    }

    /**
     * Sets the angle at the watermark will be painted, in radians. This value
     * must lie between <tt>0</tt> and <tt>PI / 2</tt> (inclusive).
     *
     * @param theta
     * This decorator's watermark angle
     */
    public void setTheta(double theta) {
        if (theta < 0
            || theta > Math.PI / 2) {
            throw new IllegalArgumentException("Theta must be between 0 nd PI / 2.");
        }

        this.theta = theta;
    }

    /**
     * Sets this decorator's flow pane to its preferred size and validates it.
     */
    private void validate() {
        flowPane.setSize(flowPane.getPreferredSize());
        flowPane.validate();
    }

    public Graphics2D prepare(Component component, Graphics2D graphics) {
        this.component = component;
        this.graphics = graphics;

        return graphics;
    }

    public void update() {
        int width = component.getWidth();
        int height = component.getHeight();

        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);

        Graphics2D watermarkGraphics = (Graphics2D)graphics.create();
        watermarkGraphics.setComposite(AlphaComposite.getInstance
            (AlphaComposite.SRC_OVER, opacity));
        watermarkGraphics.rotate(theta);

        // Calculate the separation in between each repetition of the watermark
        int dX = (int)(1.5 * flowPane.getWidth());
        int dY = 2 * flowPane.getHeight();

        // Prepare the origin of our graphics context
        int x = 0;
        int y = (int)(-width * sinTheta);
        watermarkGraphics.translate(x, y);

        for (int yStop = (int)(height * cosTheta), p = 0; y < yStop; y += dY, p = 1 - p) {
            for (int xStop = (int)(height * sinTheta + width * cosTheta); x < xStop; x += dX) {
                flowPane.paint(watermarkGraphics);
                watermarkGraphics.translate(dX, 0);
            }

            // Move X origin back to its starting position & Y origin down
            watermarkGraphics.translate(-x, dY);
            x = 0;

            // Shift the x back and forth to add randomness feel to pattern
            watermarkGraphics.translate((int)((0.5f - p) * flowPane.getWidth()), 0);
        }

        watermarkGraphics.dispose();
    }

    public Bounds getBounds(Component component) {
        return new Bounds(0, 0, component.getWidth(), component.getHeight());
    }

    public AffineTransform getTransform(Component component) {
        return new AffineTransform();
    }
}