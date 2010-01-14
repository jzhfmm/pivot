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
package pivot.wtk;

import pivot.collections.Dictionary;

/**
 * Class representing the corner radii of a rectangular object.
 *
 * @author gbrown
 */
public class CornerRadii {
    public int topLeft = 0;
    public int topRight = 0;
    public int bottomLeft = 0;
    public int bottomRight = 0;

    public static final String TOP_LEFT_KEY = "topLeft";
    public static final String TOP_RIGHT_KEY = "topRight";
    public static final String BOTTOM_LEFT_KEY = "bottomLeft";
    public static final String BOTTOM_RIGHT_KEY = "bottomRight";

    public CornerRadii() {
    }

    public CornerRadii(int radius) {
        this(radius, radius, radius, radius);
    }

    public CornerRadii(Dictionary<String, ?> cornerRadii) {
        if (cornerRadii == null) {
            throw new IllegalArgumentException("cornerRadii is null.");
        }

        if (cornerRadii.containsKey(TOP_LEFT_KEY)) {
            topLeft = (Integer)cornerRadii.get(TOP_LEFT_KEY);
        }

        if (cornerRadii.containsKey(TOP_RIGHT_KEY)) {
            topRight = (Integer)cornerRadii.get(TOP_RIGHT_KEY);
        }

        if (cornerRadii.containsKey(BOTTOM_LEFT_KEY)) {
            bottomLeft = (Integer)cornerRadii.get(BOTTOM_LEFT_KEY);
        }

        if (cornerRadii.containsKey(BOTTOM_RIGHT_KEY)) {
            bottomRight = (Integer)cornerRadii.get(BOTTOM_RIGHT_KEY);
        }
    }

    public CornerRadii(CornerRadii cornerRadii) {
        if (cornerRadii == null) {
            throw new IllegalArgumentException("cornerRadii is null.");
        }

        this.topLeft = cornerRadii.topLeft;
        this.topRight = cornerRadii.topRight;
        this.bottomLeft = cornerRadii.bottomLeft;
        this.bottomRight = cornerRadii.bottomRight;
    }

    public CornerRadii(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public boolean equals(Object object) {
        boolean equals = false;

        if (object instanceof CornerRadii) {
            CornerRadii cornerRadii = (CornerRadii)object;
            equals = (topLeft == cornerRadii.topLeft
                && topRight == cornerRadii.topRight
                && bottomLeft == cornerRadii.bottomLeft
                && bottomRight == cornerRadii.bottomRight);
        }

        return equals;
    }

    public String toString() {
        return getClass().getName() + " [" + topLeft + ", " + topRight
            + bottomLeft + ", " + bottomRight + "]";
    }
}