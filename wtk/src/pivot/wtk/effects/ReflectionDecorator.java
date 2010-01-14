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
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import pivot.wtk.Component;
import pivot.wtk.Bounds;

/**
 * Decorator that paints a reflection of a component.
 * <p>
 * TODO Make gradient properties configurable.
 * <p>
 * TODO Add a shear value.
 *
 * @author gbrown
 */
public class ReflectionDecorator implements Decorator {
    private Graphics2D graphics = null;
    private Component component = null;

    private BufferedImage componentImage = null;
    private Graphics2D componentImageGraphics = null;

    public Graphics2D prepare(Component component, Graphics2D graphics) {
        this.graphics = graphics;
        this.component = component;

        int width = component.getWidth();
        int height = component.getHeight();

        componentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        componentImageGraphics = componentImage.createGraphics();

        // Clear the image background
        componentImageGraphics.setComposite(AlphaComposite.Clear);
        componentImageGraphics.fillRect(0, 0, componentImage.getWidth(), componentImage.getHeight());

        componentImageGraphics.setComposite(AlphaComposite.SrcOver);

        return componentImageGraphics;
    }

    public void update() {
        // Draw the component
        graphics.drawImage(componentImage, 0, 0, null);

        // Draw the reflection
        int width = componentImage.getWidth();
        int height = componentImage.getHeight();

        GradientPaint mask = new GradientPaint(0, height / 4, new Color(1.0f, 1.0f, 1.0f, 0.0f),
            0, height, new Color(1.0f, 1.0f, 1.0f, 0.5f));
        componentImageGraphics.setPaint(mask);

        componentImageGraphics.setComposite(AlphaComposite.DstIn);
        componentImageGraphics.fillRect(0, 0, width, height);

        componentImageGraphics.dispose();
        componentImage.flush();

        graphics.transform(getTransform(component));

        graphics.drawImage(componentImage, 0, 0, null);
    }

    public Bounds getBounds(Component component) {
        return new Bounds(0, 0, component.getWidth(), component.getHeight() * 2);
    }

    public AffineTransform getTransform(Component component) {
        AffineTransform transform = AffineTransform.getScaleInstance(1.0, -1.0);
        transform.translate(0, -(component.getHeight() * 2));

        return transform;
    }
}