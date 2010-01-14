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

import pivot.util.ListenerList;

/**
 * Window representing a "tooltip". Tooltips are used to provide additional
 * context information to a user. A tooltip generally appears after a certain
 * amount of time has passed and closes when the user moves the mouse.
 *
 * @author gbrown
 */
public class Tooltip extends Window {
    private static class TooltipListenerList extends ListenerList<TooltipListener>
        implements TooltipListener {
        public void textChanged(Tooltip tooltip, String previousText) {
            for (TooltipListener listener : this) {
                listener.textChanged(tooltip, previousText);
            }
        }
    }

    private String text = null;

    private TooltipListenerList tooltipListeners = new TooltipListenerList();

    public Tooltip(String text) {
        super(true);

        setText(text);
        installSkin(Tooltip.class);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String previousText = this.text;

        if (previousText != text) {
            this.text = text;
            tooltipListeners.textChanged(this, previousText);
        }
    }

    public ListenerList<TooltipListener> getTooltipListeners() {
        return tooltipListeners;
    }
}