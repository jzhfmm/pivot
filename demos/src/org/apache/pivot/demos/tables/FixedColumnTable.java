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
package org.apache.pivot.demos.tables;

import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.TableViewSelectionListener;
import org.apache.pivot.wtk.TableViewSortListener;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.content.TableViewRowComparator;
import org.apache.pivot.wtkx.WTKXSerializer;

public class FixedColumnTable implements Application {
    private Window window = null;
    private TableView primaryTableView = null;
    private TableView fixedTableView = null;

    private boolean synchronizingSelection = false;

    @Override
    public void startup(Display display, Map<String, String> properties)
        throws Exception {
        WTKXSerializer wtkxSerializer = new WTKXSerializer();
        window = (Window)wtkxSerializer.readObject(this, "fixed_column_table.wtkx");
        primaryTableView = (TableView)wtkxSerializer.get("primaryTableView");
        fixedTableView = (TableView)wtkxSerializer.get("fixedTableView");

        // Keep selection state in sync
        primaryTableView.getTableViewSelectionListeners().add(new TableViewSelectionListener() {
            @Override
            public void selectedRangeAdded(TableView tableView, int rangeStart, int rangeEnd) {
                if (!synchronizingSelection) {
                    synchronizingSelection = true;
                    fixedTableView.addSelectedRange(rangeStart, rangeEnd);
                    synchronizingSelection = false;
                }
            }

            @Override
            public void selectedRangeRemoved(TableView tableView, int rangeStart, int rangeEnd) {
                if (!synchronizingSelection) {
                    synchronizingSelection = true;
                    fixedTableView.removeSelectedRange(rangeStart, rangeEnd);
                    synchronizingSelection = false;
                }
            }

            @Override
            public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
                if (!synchronizingSelection) {
                    synchronizingSelection = true;
                    fixedTableView.setSelectedRanges(tableView.getSelectedRanges());
                    synchronizingSelection = false;
                }
            }
        });

        fixedTableView.getTableViewSelectionListeners().add(new TableViewSelectionListener() {
            @Override
            public void selectedRangeAdded(TableView tableView, int rangeStart, int rangeEnd) {
                if (!synchronizingSelection) {
                    synchronizingSelection = true;
                    primaryTableView.addSelectedRange(rangeStart, rangeEnd);
                    synchronizingSelection = false;
                }
            }

            @Override
            public void selectedRangeRemoved(TableView tableView, int rangeStart, int rangeEnd) {
                if (!synchronizingSelection) {
                    synchronizingSelection = true;
                    primaryTableView.removeSelectedRange(rangeStart, rangeEnd);
                    synchronizingSelection = false;
                }
            }

            @Override
            public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
                if (!synchronizingSelection) {
                    synchronizingSelection = true;
                    primaryTableView.setSelectedRanges(tableView.getSelectedRanges());
                    synchronizingSelection = false;
                }
            }
        });

        // Keep header state in sync
        primaryTableView.getTableViewSortListeners().add(new TableViewSortListener.Adapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void sortChanged(TableView tableView) {
                if (!tableView.getSort().isEmpty()) {
                    fixedTableView.clearSort();
                }

                List<Object> tableData = (List<Object>)tableView.getTableData();
                tableData.setComparator(new TableViewRowComparator(tableView));
            }
        });

        fixedTableView.getTableViewSortListeners().add(new TableViewSortListener.Adapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void sortChanged(TableView tableView) {
                if (!tableView.getSort().isEmpty()) {
                    primaryTableView.clearSort();
                }

                List<Object> tableData = (List<Object>)tableView.getTableData();
                tableData.setComparator(new TableViewRowComparator(tableView));
            }
        });

        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    public static void main(String[] args) {
        DesktopApplicationContext.main(FixedColumnTable.class, args);
    }
}
