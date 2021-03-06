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
package org.apache.pivot.wtk;

import org.apache.pivot.collections.Sequence;

/**
 * List view selection listener interface.
 */
public interface ListViewSelectionListener {
    /**
     * List view selection listener adapter.
     */
    public static class Adapter implements ListViewSelectionListener {
        @Override
        public void selectedRangeAdded(ListView listView, int rangeStart, int rangeEnd) {
        }

        @Override
        public void selectedRangeRemoved(ListView listView, int rangeStart, int rangeEnd) {
        }

        @Override
        public void selectedRangesChanged(ListView listView, Sequence<Span> previousSelectedRanges) {
        }
    }

    /**
     * Called when a range has been added to a list view's selection.
     *
     * @param listView
     * @param rangeStart
     * @param rangeEnd
     */
    public void selectedRangeAdded(ListView listView, int rangeStart, int rangeEnd);

    /**
     * Called when a range has been removed from a list view's selection.
     *
     * @param listView
     * @param rangeStart
     * @param rangeEnd
     */
    public void selectedRangeRemoved(ListView listView, int rangeStart, int rangeEnd);

    /**
     * Called when a list view's selection state has been reset.
     *
     * @param listView
     * @param previousSelectedRanges
     */
    public void selectedRangesChanged(ListView listView, Sequence<Span> previousSelectedRanges);
}
