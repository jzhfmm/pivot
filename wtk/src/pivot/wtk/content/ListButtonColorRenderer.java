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
package pivot.wtk.content;

import java.awt.Color;

import pivot.wtk.Button;
import pivot.wtk.ImageView;

/**
 * List button renderer for displaying color swatches.
 *
 * @author gbrown
 */
public class ListButtonColorRenderer extends ImageView
	implements Button.DataRenderer {
    private ListViewColorRenderer.ColorBadge colorBadge =
    	new ListViewColorRenderer.ColorBadge();

    public ListButtonColorRenderer() {
    	setImage(colorBadge);
    }

    public void render(Object data, Button button, boolean highlighted) {
    	Color color;
    	if (data instanceof ColorItem) {
    		ColorItem colorItem = (ColorItem)data;
    		color = colorItem.getColor();
    	} else {
        	if (data instanceof Color) {
        		color = (Color)data;
        	} else {
        		color = Color.decode(data.toString());
        	}
    	}

    	colorBadge.setColor(color);
    }
}