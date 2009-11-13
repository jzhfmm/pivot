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

import java.util.Iterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.ImmutableIterator;
import org.apache.pivot.util.ListenerList;


/**
 * Container that arranges components in a two-dimensional grid, where every cell is the same size.
 */
public class GridPane extends Container {
    /**
     * Represents a grid pane row.
     */
    public static final class Row implements Sequence<Component>, Iterable<Component> {
        private boolean highlighted;

        private ArrayList<Component> cells = new ArrayList<Component>();

        private GridPane gridPane = null;

        public Row() {
            this(false);
        }

        public Row(boolean highlighted) {
            this.highlighted = highlighted;
        }

        /**
         * Returns the grid pane with which this row is associated.
         *
         * @return
         * The row's grid pane, or <tt>null</tt> if the row does not
         * currently belong to a grid.
         */
        public GridPane getGridPane() {
            return gridPane;
        }

        /**
         * Sets the grid pane with which this row is associated.
         *
         * @param gridPane
         * The row's grid pane, or <tt>null</tt> if the row does not
         * currently belong to a grid.
         */
        private void setGridPane(GridPane gridPane) {
            this.gridPane = gridPane;
        }

        /**
         * Returns the highlighted flag.
         *
         * @return
         * <tt>true</tt> if the row is highlighted, <tt>false</tt> if it is not
         */
        public boolean isHighlighted() {
            return highlighted;
        }

        /**
         * Sets the highlighted flag.
         *
         * @param highlighted
         * <tt>true</tt> to set the row as highlighted, <tt>false</tt> to set
         * it as not highlighted
         */
        public void setHighlighted(boolean highlighted) {
            if (highlighted != this.highlighted) {
                this.highlighted = highlighted;

                if (gridPane != null) {
                    gridPane.gridPaneListeners.rowHighlightedChanged(this);
                }
            }
        }

        /**
         * Sets the visible flag for all components in the row.
         * <p>
         * This is a convenience method that iterates through the row, calling
         * <tt>setVisible</tt> on all components.
         */
        public void setVisible(boolean visible) {
            if (gridPane != null) {
                for (Component component : cells) {
                    component.setVisible(visible);
                }
            }
        }

        @Override
        public int add(Component component) {
            int i = getLength();
            insert(component, i);

            return i;
        }

        @Override
        public void insert(Component component, int index) {
            if (component == null) {
                throw new IllegalArgumentException("Component is null.");
            }

            if (component.getParent() != null) {
                throw new IllegalArgumentException("Component already has a parent.");
            }

            cells.insert(component, index);

            if (gridPane != null) {
                gridPane.add(component);
                gridPane.gridPaneListeners.cellInserted(this, index);
            }
        }

        @Override
        public Component update(int index, Component component) {
            Component previousComponent = cells.get(index);

            if (component != previousComponent) {
                if (component == null) {
                    throw new IllegalArgumentException("Component is null.");
                }

                if (component.getParent() != null) {
                    throw new IllegalArgumentException("Component already has a parent.");
                }

                cells.update(index, component);
                previousComponent.setAttributes(null);

                if (gridPane != null) {
                    gridPane.add(component);
                    gridPane.gridPaneListeners.cellUpdated(this, index, previousComponent);
                    gridPane.remove(previousComponent);
                }
            }

            return previousComponent;
        }

        @Override
        public int remove(Component component) {
            int index = indexOf(component);
            if (index != -1) {
                remove(index, 1);
            }

            return index;
        }

        @Override
        public Sequence<Component> remove(int index, int count) {
            Sequence<Component> removed = cells.remove(index, count);

            for (int i = 0, n = removed.getLength(); i < n; i++) {
                Component component = removed.get(i);
                component.setAttributes(null);
            }

            if (gridPane != null) {
                gridPane.gridPaneListeners.cellsRemoved(this, index, removed);

                for (int i = 0, n = removed.getLength(); i < n; i++) {
                    Component component = removed.get(i);
                    gridPane.remove(component);
                }
            }

            return removed;
        }

        @Override
        public Component get(int index) {
            return cells.get(index);
        }

        @Override
        public int indexOf(Component component) {
            return cells.indexOf(component);
        }

        @Override
        public int getLength() {
            return cells.getLength();
        }

        @Override
        public Iterator<Component> iterator() {
            return new ImmutableIterator<Component>(cells.iterator());
        }
    }

    /**
     * Represents a grid pane column.
     */
    public static class Column {
        private GridPane gridPane = null;

        private boolean highlighted;

        public Column() {
            this(false);
        }

        public Column(boolean highlighted) {
            this.highlighted = highlighted;
        }

        /**
         * Returns the grid pane with which this column is associated.
         *
         * @return
         * The column's grid pane, or <tt>null</tt> if the column does not
         * currently belong to a grid.
         */
        public GridPane getGridPane() {
            return gridPane;
        }

        /**
         * Sets the grid pane with which this column is associated.
         *
         * @param gridPane
         * The column's grid pane, or <tt>null</tt> if the column does not
         * currently belong to a grid.
         */
        private void setGridPane(GridPane gridPane) {
            this.gridPane = gridPane;
        }

        /**
         * Returns the highlighted flag.
         *
         * @return
         * <tt>true</tt> if the column is highlighted, <tt>false</tt> if it is not
         */
        public boolean isHighlighted() {
            return highlighted;
        }

        /**
         * Sets the highlighted flag.
         *
         * @param highlighted
         * <tt>true</tt> to set the column as highlighted, <tt>false</tt> to set
         * it as not highlighted
         */
        public void setHighlighted(boolean highlighted) {
            if (highlighted != this.highlighted) {
                this.highlighted = highlighted;

                if (gridPane != null) {
                    gridPane.gridPaneListeners.columnHighlightedChanged(this);
                }
            }
        }

        /**
         * Sets the visible flag for all components in the column.
         * <p>
         * This is a convenience method that iterates through the components in
         * the column, calling <tt>setVisible</tt> on all such components.
         */
        public void setVisible(boolean visible) {
            if (gridPane != null) {
                int columnIndex = gridPane.columns.indexOf(this);

                for (Row row : gridPane.rows) {
                    if (row.getLength() > columnIndex) {
                        row.get(columnIndex).setVisible(visible);
                    }
                }
            }
        }
    }

    /**
     * grid pane skin interface. grid pane skins must implement
     * this interface to facilitate additional communication between the
     * component and the skin.
     */
    public interface Skin {
        public int getRowAt(int y);
        public Bounds getRowBounds(int row);
        public int getColumnAt(int x);
        public Bounds getColumnBounds(int column);
    }

    /**
     * Class that manages a grid pane's row list. Callers get access to the
     * row sequence via {@link GridPane#getRows()}.
     */
    public final class RowSequence implements Sequence<Row>, Iterable<Row> {
        private RowSequence() {
        }

        @Override
        public int add(Row row) {
            int i = getLength();
            insert(row, i);

            return i;
        }

        @Override
        public void insert(Row row, int index) {
            if (row == null) {
                throw new IllegalArgumentException("row is null.");
            }

            if (row.getGridPane() != null) {
                throw new IllegalArgumentException
                    ("row is already in use by another grid pane.");
            }

            rows.insert(row, index);
            row.setGridPane(GridPane.this);

            for (int i = 0, n = row.getLength(); i < n; i++) {
                Component component = row.get(i);
                GridPane.this.add(component);
            }

            // Notify listeners
            gridPaneListeners.rowInserted(GridPane.this, index);
        }

        @Override
        public Row update(int index, Row row) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remove(Row row) {
            int index = indexOf(row);
            if (index != -1) {
                remove(index, 1);
            }

            return index;
        }

        @Override
        public Sequence<Row> remove(int index, int count) {
            Sequence<Row> removed = rows.remove(index, count);

            if (count > 0) {
                for (int i = 0, n = removed.getLength(); i < n; i++) {
                    Row row = removed.get(i);
                    row.setGridPane(null);

                    for (int j = 0, m = row.getLength(); j < m; j++) {
                        Component component = row.get(j);
                        GridPane.this.remove(component);
                    }
                }

                gridPaneListeners.rowsRemoved(GridPane.this, index, removed);
            }

            return removed;
        }

        @Override
        public Row get(int index) {
            return rows.get(index);
        }

        @Override
        public int indexOf(Row row) {
            return rows.indexOf(row);
        }

        @Override
        public int getLength() {
            return rows.getLength();
        }

        @Override
        public Iterator<Row> iterator() {
            return new ImmutableIterator<Row>(rows.iterator());
        }
    }

    /**
     * Class that manages a grid pane's column list. Callers get access to the
     * column sequence via {@link GridPane#getColumns()}.
     */
    public final class ColumnSequence implements Sequence<Column>, Iterable<Column> {
        private ColumnSequence() {
        }

        @Override
        public int add(Column column) {
            int i = getLength();
            insert(column, i);

            return i;
        }

        @Override
        public void insert(Column column, int index) {
            if (column == null) {
                throw new IllegalArgumentException("column is null.");
            }

            if (column.getGridPane() != null) {
                throw new IllegalArgumentException
                    ("column is already in use by another grid pane.");
            }

            columns.insert(column, index);
            column.setGridPane(GridPane.this);

            // Notify listeners
            gridPaneListeners.columnInserted(GridPane.this, index);
        }

        @Override
        public Column update(int index, Column column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remove(Column column) {
            int index = indexOf(column);
            if (index != -1) {
                remove(index, 1);
            }

            return index;
        }

        @Override
        public Sequence<Column> remove(int index, int count) {
            Sequence<Column> removed = columns.remove(index, count);

            if (count > 0) {
                for (int i = 0, n = removed.getLength(); i < n; i++) {
                    Column column = removed.get(i);
                    column.setGridPane(null);
                }

                gridPaneListeners.columnsRemoved(GridPane.this, index, removed);
            }

            return removed;
        }

        @Override
        public Column get(int index) {
            return columns.get(index);
        }

        @Override
        public int indexOf(Column column) {
            return columns.indexOf(column);
        }

        @Override
        public int getLength() {
            return columns.getLength();
        }

        @Override
        public Iterator<Column> iterator() {
            return new ImmutableIterator<Column>(columns.iterator());
        }
    }

    /**
     * Component that can be used as filler for empty cells.
     */
    public static class Filler extends Component {
        public Filler() {
            installThemeSkin(Filler.class);
        }
    }

    private static class GridPaneListenerList extends ListenerList<GridPaneListener>
        implements GridPaneListener {
        @Override
        public void rowInserted(GridPane gridPane, int index) {
            for (GridPaneListener listener : this) {
                listener.rowInserted(gridPane, index);
            }
        }

        @Override
        public void rowsRemoved(GridPane gridPane, int index,
            Sequence<GridPane.Row> rows) {
            for (GridPaneListener listener : this) {
                listener.rowsRemoved(gridPane, index, rows);
            }
        }

        @Override
        public void rowHighlightedChanged(GridPane.Row row) {
            for (GridPaneListener listener : this) {
                listener.rowHighlightedChanged(row);
            }
        }

        @Override
        public void columnInserted(GridPane gridPane, int index) {
            for (GridPaneListener listener : this) {
                listener.columnInserted(gridPane, index);
            }
        }

        @Override
        public void columnsRemoved(GridPane gridPane, int index,
            Sequence<GridPane.Column> columns) {
            for (GridPaneListener listener : this) {
                listener.columnsRemoved(gridPane, index, columns);
            }
        }

        @Override
        public void columnHighlightedChanged(GridPane.Column column) {
            for (GridPaneListener listener : this) {
                listener.columnHighlightedChanged(column);
            }
        }

        @Override
        public void cellInserted(GridPane.Row row, int column) {
            for (GridPaneListener listener : this) {
                listener.cellInserted(row, column);
            }
        }

        @Override
        public void cellsRemoved(GridPane.Row row, int column,
            Sequence<Component> removed) {
            for (GridPaneListener listener : this) {
                listener.cellsRemoved(row, column, removed);
            }
        }

        @Override
        public void cellUpdated(GridPane.Row row, int column,
            Component previousComponent) {
            for (GridPaneListener listener : this) {
                listener.cellUpdated(row, column, previousComponent);
            }
        }
    }

    private ArrayList<Row> rows = null;
    private RowSequence rowSequence = new RowSequence();

    private ArrayList<Column> columns = null;
    private ColumnSequence columnSequence = new ColumnSequence();

    private GridPaneListenerList gridPaneListeners = new GridPaneListenerList();

    public static final String RELATIVE_SIZE_INDICATOR = "*";

    /**
     * Creates a new <tt>GridPane</tt> with empty row and column sequences.
     */
    public GridPane() {
        this(new ArrayList<Column>());
    }

    /**
     * Creates a new <tt>GridPane</tt> with the specified columns.
     *
     * @param columns
     * The column sequence to use. A copy of this sequence will be made
     */
    public GridPane(Sequence<Column> columns) {
        if (columns == null) {
            throw new IllegalArgumentException("columns is null");
        }

        this.rows = new ArrayList<Row>();
        this.columns = new ArrayList<Column>(columns);

        installThemeSkin(GridPane.class);
    }

    @Override
    protected void setSkin(org.apache.pivot.wtk.Skin skin) {
        if (!(skin instanceof GridPane.Skin)) {
            throw new IllegalArgumentException("Skin class must implement "
                + GridPane.Skin.class.getName());
        }

        super.setSkin(skin);
    }

    /**
     * Returns the grid pane row sequence.
     *
     * @return
     * The grid pane row sequence
     */
    public RowSequence getRows() {
        return rowSequence;
    }

    /**
     * Returns the index of the row at a given location.
     *
     * @param y
     * The y-coordinate of the row to identify.
     *
     * @return
     * The row index, or <tt>-1</tt> if there is no row at the given
     * y-coordinate.
     */
    public int getRowAt(int y) {
        GridPane.Skin gridPaneSkin = (GridPane.Skin)getSkin();
        return gridPaneSkin.getRowAt(y);
    }

    /**
     * Returns the bounds of a given row.
     *
     * @param row
     * The row index.
     */
    public Bounds getRowBounds(int row) {
        GridPane.Skin gridPaneSkin = (GridPane.Skin)getSkin();
        return gridPaneSkin.getRowBounds(row);
    }

    /**
     * Returns the grid pane column sequence.
     *
     * @return
     * The grid pane column sequence
     */
    public ColumnSequence getColumns() {
        return columnSequence;
    }

    /**
     * Returns the index of the column at a given location.
     *
     * @param x
     * The x-coordinate of the column to identify.
     *
     * @return
     * The column index, or <tt>-1</tt> if there is no column at the given
     * x-coordinate.
     */
    public int getColumnAt(int x) {
        GridPane.Skin gridPaneSkin = (GridPane.Skin)getSkin();
        return gridPaneSkin.getColumnAt(x);
    }

    /**
     * Returns the bounds of a given column.
     *
     * @param column
     * The column index.
     */
    public Bounds getColumnBounds(int column) {
        GridPane.Skin gridPaneSkin = (GridPane.Skin)getSkin();
        return gridPaneSkin.getColumnBounds(column);
    }

    /**
     * Gets the component at the specified cell in this grid pane.
     *
     * @param rowIndex
     * The row index of the cell
     *
     * @param columnIndex
     * The column index of the cell
     *
     * @return
     * The component in the specified cell, or <tt>null</tt> if the cell is
     * empty
     */
    public Component getCellComponent(int rowIndex, int columnIndex) {
        Row row = rows.get(rowIndex);

        Component component = null;

        if (row.getLength() > columnIndex) {
            component = row.get(columnIndex);
        }

        return component;
    }

    /**
     * Sets the component at the specified cell in this grid pane.
     *
     * @param row
     * The row index of the cell
     *
     * @param column
     * The column index of the cell
     *
     * @param component
     * The component to place in the specified cell, or <tt>null</tt> to empty
     * the cell
     */
    public void setCellComponent(int row, int column, Component component) {
        rows.get(row).update(column, component);
    }

    /**
     * Overrides the base method to check whether or not a cell component is
     * being removed, and fires the appropriate event in that case.
     *
     * @param index
     * The index at which components were removed
     *
     * @param count
     * The number of components removed
     *
     * @return
     * The sequence of components that were removed
     */
    @Override
    public Sequence<Component> remove(int index, int count) {
        for (int i = index, n = index + count; i < n; i++) {
            Component component = get(i);

            for (Row row : rows) {
                if (row.indexOf(component) >= 0) {
                    throw new UnsupportedOperationException();
                }
            }
        }

        // Call the base method to remove the components
        return super.remove(index, count);
    }

    /**
     * Returns the grid pane listener list.
     */
    public ListenerList<GridPaneListener> getGridPaneListeners() {
        return gridPaneListeners;
    }


}
