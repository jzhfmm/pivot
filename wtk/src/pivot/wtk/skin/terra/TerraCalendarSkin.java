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
package pivot.wtk.skin.terra;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import pivot.util.CalendarDate;
import pivot.wtk.Bounds;
import pivot.wtk.Button;
import pivot.wtk.Calendar;
import pivot.wtk.CalendarListener;
import pivot.wtk.CalendarSelectionListener;
import pivot.wtk.Component;
import pivot.wtk.ComponentMouseButtonListener;
import pivot.wtk.Cursor;
import pivot.wtk.Dimensions;
import pivot.wtk.FlowPane;
import pivot.wtk.HorizontalAlignment;
import pivot.wtk.Insets;
import pivot.wtk.Keyboard;
import pivot.wtk.Label;
import pivot.wtk.Mouse;
import pivot.wtk.Spinner;
import pivot.wtk.SpinnerSelectionListener;
import pivot.wtk.TablePane;
import pivot.wtk.Theme;
import pivot.wtk.Button.Group;
import pivot.wtk.content.ButtonDataRenderer;
import pivot.wtk.content.NumericSpinnerData;
import pivot.wtk.content.SpinnerItemRenderer;
import pivot.wtk.skin.ButtonSkin;
import pivot.wtk.skin.CalendarSkin;

/**
 * Terra calendar skin.
 *
 * @author gbrown
 */
public class TerraCalendarSkin extends CalendarSkin
    implements CalendarListener, CalendarSelectionListener {
    public class DateButton extends Button {
        public DateButton() {
            super(null);

            super.setToggleButton(true);
            setDataRenderer(dateButtonDataRenderer);

            setSkin(new DateButtonSkin());
        }

        public void press() {
            setSelected(true);

            super.press();
        }

        @Override
        public void setToggleButton(boolean toggleButton) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTriState(boolean triState) {
            throw new UnsupportedOperationException();
        }
    }

    public class DateButtonSkin extends ButtonSkin {
        @Override
        public void install(Component component) {
            super.install(component);

            component.setCursor(Cursor.DEFAULT);
        }

        public int getPreferredWidth(int height) {
            DateButton dateButton = (DateButton)getComponent();

            int preferredWidth = 0;

            Button.DataRenderer dataRenderer = dateButton.getDataRenderer();
            dataRenderer.render(dateButton.getButtonData(), dateButton, false);

            preferredWidth = dataRenderer.getPreferredWidth(height) + padding * 2;

            return preferredWidth;
        }

        public int getPreferredHeight(int width) {
            int preferredHeight = 0;

            DateButton dateButton = (DateButton)getComponent();

            Button.DataRenderer dataRenderer = dateButton.getDataRenderer();
            dataRenderer.render(dateButton.getButtonData(), dateButton, false);

            preferredHeight = dataRenderer.getPreferredHeight(width) + padding * 2;

            return preferredHeight;
        }

        public Dimensions getPreferredSize() {
            DateButton dateButton = (DateButton)getComponent();

            Button.DataRenderer dataRenderer = dateButton.getDataRenderer();
            dataRenderer.render(dateButton.getButtonData(), dateButton, false);

            Dimensions preferredSize = dataRenderer.getPreferredSize();

            preferredSize.width += padding * 2;
            preferredSize.height += padding * 2;

            return preferredSize;
        }

        public void paint(Graphics2D graphics) {
            DateButton dateButton = (DateButton)getComponent();

            int width = getWidth();
            int height = getHeight();

            // Paint the background
            if (dateButton.isSelected()) {
                graphics.setPaint(new GradientPaint(width / 2, 0, selectionBevelColor,
                    width / 2, height, selectionBackgroundColor));

                graphics.fillRect(0, 0, width, height);
            } else {
                if (highlighted) {
                    graphics.setColor(highlightBackgroundColor);
                    graphics.fillRect(0, 0, width, height);
                }
            }

            // Paint a border if this button represents today
            CalendarDate date = (CalendarDate)dateButton.getButtonData();
            if (date.equals(today)) {
                graphics.setColor(dividerColor);
                graphics.drawRect(0, 0, width - 1, height - 1);
            }

            // Paint the content
            Button.DataRenderer dataRenderer = dateButton.getDataRenderer();
            dataRenderer.render(date, dateButton, highlighted);
            dataRenderer.setSize(width - padding * 2, height - padding * 2);

            graphics.translate(padding, padding);
            dataRenderer.paint(graphics);
        }

        public Font getFont() {
            return font;
        }

        public Color getColor() {
            return color;
        }

        public Color getDisabledColor() {
            return disabledColor;
        }

        public Color getSelectionColor() {
            return selectionColor;
        }

        @Override
        public void focusedChanged(Component component, boolean temporary) {
            super.focusedChanged(component, temporary);

            highlighted = component.isFocused();
        }

        @Override
        public void mouseOver(Component component) {
            super.mouseOver(component);

            Calendar calendar = (Calendar)TerraCalendarSkin.this.getComponent();

            if (calendar.containsFocus()) {
                component.requestFocus();
            }
        }

        @Override
        public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
            boolean consumed = super.mouseClick(component, button, x, y, count);

            DateButton dateButton = (DateButton)getComponent();
            dateButton.requestFocus();
            dateButton.press();

            return consumed;
        }

        @Override
        public boolean keyPressed(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
            boolean consumed = false;

            DateButton dateButton = (DateButton)getComponent();

            if (keyCode == Keyboard.KeyCode.ENTER) {
                dateButton.press();
            } else if (keyCode == Keyboard.KeyCode.UP
                || keyCode == Keyboard.KeyCode.DOWN
                || keyCode == Keyboard.KeyCode.LEFT
                || keyCode == Keyboard.KeyCode.RIGHT) {
                CalendarDate date = (CalendarDate)dateButton.getButtonData();

                int cellIndex = getCellIndex(date.getYear(), date.getMonth(), date.getDay());
                int rowIndex = cellIndex / 7;
                int columnIndex = cellIndex % 7;

                Component nextButton;
                switch (keyCode) {
                    case Keyboard.KeyCode.UP: {
                        do {
                            rowIndex--;
                            if (rowIndex < 0) {
                                rowIndex = 5;
                            }

                            TablePane.Row row = tablePane.getRows().get(rowIndex + 2);
                            nextButton = row.get(columnIndex);
                        } while (!nextButton.isEnabled());

                        nextButton.requestFocus();
                        break;
                    }

                    case Keyboard.KeyCode.DOWN: {
                        do {
                            rowIndex++;
                            if (rowIndex > 5) {
                                rowIndex = 0;
                            }

                            TablePane.Row row = tablePane.getRows().get(rowIndex + 2);
                            nextButton = row.get(columnIndex);
                        } while (!nextButton.isEnabled());

                        nextButton.requestFocus();
                        break;
                    }

                    case Keyboard.KeyCode.LEFT: {
                        TablePane.Row row = tablePane.getRows().get(rowIndex + 2);

                        do {
                            columnIndex--;
                            if (columnIndex < 0) {
                                columnIndex = 6;
                            }

                            nextButton = row.get(columnIndex);
                        } while (!nextButton.isEnabled());

                        nextButton.requestFocus();
                        break;
                    }

                    case Keyboard.KeyCode.RIGHT: {
                        TablePane.Row row = tablePane.getRows().get(rowIndex + 2);

                        do {
                            columnIndex++;
                            if (columnIndex > 6) {
                                columnIndex = 0;
                            }

                            nextButton = row.get(columnIndex);
                        } while (!nextButton.isEnabled());

                        nextButton.requestFocus();
                        break;
                    }
                }

                consumed = true;
            } else {
                consumed = super.keyPressed(component, keyCode, keyLocation);
            }

            return consumed;
        }

        @Override
        public boolean keyReleased(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
            boolean consumed = false;

            DateButton dateButton = (DateButton)getComponent();

            if (keyCode == Keyboard.KeyCode.SPACE) {
                dateButton.press();
                consumed = true;
            } else {
                consumed = super.keyReleased(component, keyCode, keyLocation);
            }

            return consumed;
        }
    }

    public class MonthSpinnerItemRenderer extends SpinnerItemRenderer {
        @Override
        public void render(Object item, Spinner spinner) {
            Calendar calendar = (Calendar)getComponent();

            CalendarDate date = new CalendarDate();
            date.set(date.getYear(), (Integer)item, 0);

            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",
                calendar.getLocale());
            item = monthFormat.format(date.toCalendar().getTime());

            super.render(item, spinner);
        }
    }

    private class DateButtonDataRenderer extends ButtonDataRenderer {
        public void render(Object data, Button button, boolean highlighted) {
            CalendarDate date = (CalendarDate)data;
            super.render(date.getDay() + 1, button, highlighted);

            if (button.isSelected()) {
                label.getStyles().put("color", button.getStyles().get("selectionColor"));
            }
        }
    }

    private TablePane tablePane;
    private Spinner monthSpinner;
    private Spinner yearSpinner;

    private DateButton[][] dateButtons = new DateButton[6][7];
    private Button.Group dateButtonGroup;

    private Button.DataRenderer dateButtonDataRenderer = new DateButtonDataRenderer();

    private CalendarDate today = null;

    private Font font;
    private Color color;
    private Color disabledColor;
    private Color selectionColor;
    private Color selectionBackgroundColor;
    private Color highlightColor;
    private Color highlightBackgroundColor;
    private Color dividerColor;
    private int padding = 4;

    // Derived colors
    private Color selectionBevelColor;

    public TerraCalendarSkin() {
        TerraTheme theme = (TerraTheme)Theme.getTheme();
        font = theme.getFont();
        color = theme.getColor(1);
        disabledColor = theme.getColor(7);
        selectionColor = theme.getColor(4);
        selectionBackgroundColor = theme.getColor(19);
        highlightColor = theme.getColor(1);
        highlightBackgroundColor = theme.getColor(10);
        dividerColor = theme.getColor(9);

        selectionBevelColor = TerraTheme.brighten(selectionBackgroundColor);

        // Create the table pane
        tablePane = new TablePane();
        for (int i = 0; i < 7; i++) {
            tablePane.getColumns().add(new TablePane.Column(1, true));
        }

        // Month spinner
        monthSpinner = new Spinner();
        monthSpinner.setSpinnerData(new NumericSpinnerData(0, 11));
        monthSpinner.setItemRenderer(new MonthSpinnerItemRenderer());
        monthSpinner.setCircular(true);
        monthSpinner.getStyles().put("sizeToContent", true);

        monthSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener() {
            public void selectedIndexChanged(Spinner spinner, int previousSelectedIndex) {
                Calendar calendar = (Calendar)getComponent();
                calendar.setMonth((Integer)spinner.getSelectedItem());
            }
        });

        // Year spinner
        yearSpinner = new Spinner();
        yearSpinner.setSpinnerData(new NumericSpinnerData(0, Short.MAX_VALUE));

        yearSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener() {
            public void selectedIndexChanged(Spinner spinner, int previousSelectedIndex) {
                Calendar calendar = (Calendar)getComponent();
                calendar.setYear((Integer)spinner.getSelectedItem());
            }
        });

        // Attach a listener to consume mouse clicks
        ComponentMouseButtonListener spinnerMouseButtonListener = new ComponentMouseButtonListener() {
            public boolean mouseDown(Component component, Mouse.Button button, int x, int y) {
                return false;
            }

            public boolean mouseUp(Component component, Mouse.Button button, int x, int y) {
                return false;
            }

            public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
                return true;
            }
        };

        monthSpinner.getComponentMouseButtonListeners().add(spinnerMouseButtonListener);
        yearSpinner.getComponentMouseButtonListeners().add(spinnerMouseButtonListener);

        TablePane.Row row;

        // Add the month/year flow pane
        FlowPane monthYearFlowPane = new FlowPane();
        monthYearFlowPane.getStyles().put("padding", 3);
        monthYearFlowPane.getStyles().put("horizontalAlignment", HorizontalAlignment.JUSTIFY);

        monthYearFlowPane.add(monthSpinner);
        monthYearFlowPane.add(yearSpinner);

        row = new TablePane.Row();
        row.add(monthYearFlowPane);
        tablePane.getRows().add(row);

        TablePane.setColumnSpan(monthYearFlowPane, 7);

        // Add the day labels
        row = new TablePane.Row();
        for (int i = 0; i < 7; i++) {
            Label label = new Label();
            label.getStyles().put("fontBold", true);
            label.getStyles().put("padding", new Insets(2, 2, 4, 2));
            label.getStyles().put("horizontalAlignment", HorizontalAlignment.CENTER);
            row.add(label);
        }

        tablePane.getRows().add(row);

        // Add the buttons
        dateButtonGroup = new Button.Group();
        dateButtonGroup.getGroupListeners().add(new Button.GroupListener() {
            public void selectionChanged(Group group, Button previousSelection) {
                Calendar calendar = (Calendar)getComponent();

                Button selection = group.getSelection();
                if (selection == null) {
                    CalendarDate selectedDate = calendar.getSelectedDate();

                    // If no date was selected, or the selection changed as a
                    // result of the user toggling the date button (as opposed
                    // to changing the month or year), clear the selection
                    if (selectedDate == null
                        || (selectedDate.getYear() == yearSpinner.getSelectedIndex()
                            && selectedDate.getMonth() == monthSpinner.getSelectedIndex())) {
                        calendar.setSelectedDate((CalendarDate)null);
                    }
                } else {
                    calendar.setSelectedDate((CalendarDate)selection.getButtonData());
                }
            }
        });

        for (int j = 0; j < 6; j++) {
            row = new TablePane.Row(1, true);

            for (int i = 0; i < 7; i++) {
                DateButton dateButton = new DateButton();
                dateButtons[j][i] = dateButton;
                dateButton.setGroup(dateButtonGroup);

                row.add(dateButton);
            }

            tablePane.getRows().add(row);
        }
    }

    @Override
    public void install(Component component) {
        super.install(component);

        Calendar calendar = (Calendar)component;
        calendar.add(tablePane);

        yearSpinner.setSelectedIndex(calendar.getYear());
        monthSpinner.setSelectedIndex(calendar.getMonth());
        updateLabels();
        updateCalendar();
    }

    @Override
    public void uninstall() {
        Calendar calendar = (Calendar)getComponent();
        calendar.remove(tablePane);

        super.uninstall();
    }

    @Override
    public int getPreferredWidth(int height) {
        return tablePane.getPreferredWidth(height);
    }

    @Override
    public int getPreferredHeight(int width) {
        return tablePane.getPreferredHeight(width);
    }

    @Override
    public Dimensions getPreferredSize() {
        return tablePane.getPreferredSize();
    }

    public void layout() {
        tablePane.setSize(getWidth(), getHeight());
        tablePane.setLocation(0, 0);
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);

        int width = getWidth();
        Bounds monthYearRowBounds = tablePane.getRowBounds(0);
        graphics.setColor(highlightBackgroundColor);
        graphics.fillRect(monthYearRowBounds.x, monthYearRowBounds.y,
            monthYearRowBounds.width, monthYearRowBounds.height);

        Bounds labelRowBounds = tablePane.getRowBounds(1);

        graphics.setColor(dividerColor);
        int dividerY = labelRowBounds.y + labelRowBounds.height - 2;
        graphics.drawLine(2, dividerY, Math.max(0, width - 3), dividerY);
    }

    private void updateLabels() {
        TablePane.Row row = tablePane.getRows().get(1);

        Calendar calendar = (Calendar)getComponent();
        Locale locale = calendar.getLocale();
        GregorianCalendar gregorianCalendar = new GregorianCalendar(locale);
        SimpleDateFormat monthFormat = new SimpleDateFormat("E", locale);
        int firstDayOfWeek = gregorianCalendar.getFirstDayOfWeek();

        for (int i = 0; i < 7; i++) {
            Label label = (Label)row.get(i);
            gregorianCalendar.set(java.util.Calendar.DAY_OF_WEEK, firstDayOfWeek + i);
            String text = monthFormat.format(gregorianCalendar.getTime());
            text = Character.toString(text.charAt(0));
            label.setText(text);
        }
    }

    private void updateCalendar() {
        Calendar calendar = (Calendar)getComponent();
        int month = calendar.getMonth();
        int year = calendar.getYear();

        monthSpinner.setSelectedIndex(month);
        yearSpinner.setSelectedIndex(year);

        // Determine the first and last days of the month
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, 1);
        int firstIndex = gregorianCalendar.get(java.util.Calendar.DAY_OF_WEEK)
            - java.util.Calendar.SUNDAY;
        int lastIndex = firstIndex + gregorianCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        // Determine the last day of last month
        gregorianCalendar.add(java.util.Calendar.MONTH, -1);
        int daysLastMonth = gregorianCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 7; i++) {
                month = calendar.getMonth();
                year = calendar.getYear();

                int k = j * 7 + i;

                DateButton dateButton = dateButtons[j][i];

                int day;
                if (k < firstIndex) {
                    month--;
                    if (month < 0) {
                        month = 11;
                        year--;
                    }

                    day = daysLastMonth - (firstIndex - k);
                    dateButton.setEnabled(false);
                } else if (k >= lastIndex) {
                    month++;
                    if (month > 11) {
                        month = 0;
                        year++;
                    }

                    day = k - lastIndex;
                    dateButton.setEnabled(false);
                } else {
                    day = k - firstIndex;
                    dateButton.setEnabled(true);
                }

                dateButton.setButtonData(new CalendarDate(year, month, day));
            }
        }

        today = new CalendarDate();
    }

    private void updateSelection(CalendarDate selectedDate) {
        Calendar calendar = (Calendar)getComponent();
        Button selection = dateButtonGroup.getSelection();

        if (selectedDate == null) {
            if (selection != null) {
                selection.setSelected(false);
            }
        } else {
            int year = selectedDate.getYear();
            int month = selectedDate.getMonth();

            if (year == calendar.getYear()
                && month == calendar.getMonth()) {
                int day = selectedDate.getDay();

                // Update the button group
                int cellIndex = getCellIndex(year, month, day);
                int rowIndex = cellIndex / 7;
                int columnIndex = cellIndex % 7;

                TablePane.Row row = tablePane.getRows().get(rowIndex + 2);
                DateButton dateButton = (DateButton)row.get(columnIndex);
                dateButton.setSelected(true);
            } else {
                if (selection != null) {
                    selection.setSelected(false);
                }
            }
        }
    }

    private static int getCellIndex(int year, int month, int day) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, 1);
        int firstDay = gregorianCalendar.get(java.util.Calendar.DAY_OF_WEEK)
            - java.util.Calendar.SUNDAY;
        int cellIndex = firstDay + day;

        return cellIndex;
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

    public Color getDisabledColor() {
        return disabledColor;
    }

    public void setDisabledColor(Color disabledColor) {
        if (disabledColor == null) {
            throw new IllegalArgumentException("disabledColor is null.");
        }

        this.disabledColor = disabledColor;
        repaintComponent();
    }

    public final void setDisabledColor(String disabledColor) {
        if (disabledColor == null) {
            throw new IllegalArgumentException("disabledColor is null.");
        }

        setDisabledColor(decodeColor(disabledColor));
    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(Color selectionColor) {
        if (selectionColor == null) {
            throw new IllegalArgumentException("selectionColor is null.");
        }

        this.selectionColor = selectionColor;
        repaintComponent();
    }

    public final void setSelectionColor(String selectionColor) {
        if (selectionColor == null) {
            throw new IllegalArgumentException("selectionColor is null.");
        }

        setSelectionColor(decodeColor(selectionColor));
    }

    public Color getSelectionBackgroundColor() {
        return selectionBackgroundColor;
    }

    public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
        if (selectionBackgroundColor == null) {
            throw new IllegalArgumentException("selectionBackgroundColor is null.");
        }

        this.selectionBackgroundColor = selectionBackgroundColor;
        selectionBevelColor = TerraTheme.brighten(selectionBackgroundColor);
        repaintComponent();
    }

    public final void setSelectionBackgroundColor(String selectionBackgroundColor) {
        if (selectionBackgroundColor == null) {
            throw new IllegalArgumentException("selectionBackgroundColor is null.");
        }

        setSelectionBackgroundColor(decodeColor(selectionBackgroundColor));
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        if (highlightColor == null) {
            throw new IllegalArgumentException("highlightColor is null.");
        }

        this.highlightColor = highlightColor;
        repaintComponent();
    }

    public final void setHighlightColor(String highlightColor) {
        if (highlightColor == null) {
            throw new IllegalArgumentException("highlightColor is null.");
        }

        setHighlightColor(decodeColor(highlightColor));
    }

    public Color getHighlightBackgroundColor() {
        return highlightBackgroundColor;
    }

    public void setHighlightBackgroundColor(Color highlightBackgroundColor) {
        if (highlightBackgroundColor == null) {
            throw new IllegalArgumentException("highlightBackgroundColor is null.");
        }

        this.highlightBackgroundColor = highlightBackgroundColor;
        repaintComponent();
    }

    public final void setHighlightBackgroundColor(String highlightBackgroundColor) {
        if (highlightBackgroundColor == null) {
            throw new IllegalArgumentException("highlightBackgroundColor is null.");
        }

        setHighlightBackgroundColor(decodeColor(highlightBackgroundColor));
    }

    public Color getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(Color dividerColor) {
        if (dividerColor == null) {
            throw new IllegalArgumentException("dividerColor is null.");
        }

        this.dividerColor = dividerColor;
        repaintComponent();
    }

    public final void setDividerColor(String dividerColor) {
        if (dividerColor == null) {
            throw new IllegalArgumentException("dividerColor is null.");
        }

        setDividerColor(decodeColor(dividerColor));
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        if (padding < 0) {
            throw new IllegalArgumentException("padding is negative.");
        }

        this.padding = padding;
        invalidateComponent();
    }

    public final void setPadding(Number padding) {
        if (padding == null) {
            throw new IllegalArgumentException("padding is null.");
        }

        setPadding(padding.intValue());
    }

    // Calendar events
    @Override
    public void yearChanged(Calendar calendar, int previousYear) {
        yearSpinner.setSelectedIndex(calendar.getYear());
        updateCalendar();
        updateSelection(calendar.getSelectedDate());
    }

    @Override
    public void monthChanged(Calendar calendar, int previousMonth) {
        monthSpinner.setSelectedIndex(calendar.getMonth());
        updateCalendar();
        updateSelection(calendar.getSelectedDate());
    }

    @Override
    public void selectedDateKeyChanged(Calendar calendar,
        String previousSelectedDateKey) {
        // No-op
    }

    @Override
    public void localeChanged(Calendar calendar, Locale previousLocale) {
        super.localeChanged(calendar, previousLocale);
        updateLabels();
    }

    // Calendar selection events
    @Override
    public void selectedDateChanged(Calendar calendar, CalendarDate previousSelectedDate) {
        updateSelection(calendar.getSelectedDate());
    }
}