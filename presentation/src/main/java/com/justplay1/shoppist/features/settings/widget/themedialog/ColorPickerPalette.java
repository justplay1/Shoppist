/*
 * Copyright (C) 2016 Mkhytar Mkhoian
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.justplay1.shoppist.features.settings.widget.themedialog;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.justplay1.shoppist.R;
import com.justplay1.shoppist.features.settings.SelectThemeColorDialogFragment;


/**
 * A color picker custom view which creates an grid of color squares.  The number of squares per
 * row (and the padding between the squares) is determined by the user.
 */
public class ColorPickerPalette extends TableLayout {

    public ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener;

    private int swatchLength;
    private int marginSize;
    private int numColumns;

    public ColorPickerPalette(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerPalette(Context context) {
        super(context);
    }

    /**
     * Initialize the size, columns, and listener.  Size should be a pre-defined size (SIZE_LARGE
     * or SIZE_SMALL) from ColorPickerDialogFragment.
     */
    public void init(int size, int columns, ColorPickerSwatch.OnColorSelectedListener listener) {
        numColumns = columns;
        Resources res = getResources();
        if (size == SelectThemeColorDialogFragment.SIZE_LARGE) {
            swatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_large);
            marginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margins_large);
        } else {
            swatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_small);
            marginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margins_small);
        }
        onColorSelectedListener = listener;
    }

    private TableRow createTableRow() {
        TableRow row = new TableRow(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(params);
        return row;
    }

    /**
     * Adds swatches to table in a serpentine format.
     */
    public void drawPalette(int[] colorsPrimary, int[] colorsPrimaryDark, int selectedColor) {
        if (colorsPrimary == null) {
            return;
        }

        this.removeAllViews();
        int tableElements = 0;
        int rowElements = 0;
        int rowNumber = 0;

        // Fills the table with swatches based on the array of colors.
        TableRow row = createTableRow();
        for (int i = 0; i < colorsPrimary.length; i++) {
            final int colorPrimary = colorsPrimary[i];
            final int colorPrimaryDark = colorsPrimaryDark[i];
            tableElements++;

            View colorSwatch = createColorSwatch(colorPrimary, colorPrimaryDark, selectedColor);
            setSwatchDescription(rowNumber, tableElements, rowElements, colorPrimary == selectedColor,
                    colorSwatch);
            addSwatchToRow(row, colorSwatch, rowNumber);

            rowElements++;
            if (rowElements == numColumns) {
                addView(row);
                row = createTableRow();
                rowElements = 0;
                rowNumber++;
            }
        }

        // Create blank views to fill the row if the last row has not been filled.
        if (rowElements > 0) {
            while (rowElements != numColumns) {
                addSwatchToRow(row, createBlankSpace(), rowNumber);
                rowElements++;
            }
            addView(row);
        }
    }

    /**
     * Appends a swatch to the end of the row for even-numbered rows (starting with row 0),
     * to the beginning of a row for odd-numbered rows.
     */
    private void addSwatchToRow(TableRow row, View swatch, int rowNumber) {
        if (rowNumber % 2 == 0) {
            row.addView(swatch);
        } else {
            row.addView(swatch, 0);
        }
    }

    /**
     * Add a content description to the specified swatch view. Because the colors get added in a
     * snaking form, every other row will need to compensate for the fact that the colors are added
     * in an opposite direction from their left->right/top->bottom order, which is how the system
     * will arrange them for accessibility purposes.
     */
    private void setSwatchDescription(int rowNumber, int index, int rowElements, boolean selected, View swatch) {
        int accessibilityIndex;
        if (rowNumber % 2 == 0) {
            // We're in a regular-ordered row
            accessibilityIndex = index;
        } else {
            // We're in a backwards-ordered row.
            int rowMax = ((rowNumber + 1) * numColumns);
            accessibilityIndex = rowMax - rowElements;
        }
    }

    /**
     * Creates a blank space to fill the row.
     */
    private ImageView createBlankSpace() {
        ImageView view = new ImageView(getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(swatchLength, swatchLength);
        params.setMargins(marginSize, marginSize, marginSize, marginSize);
        view.setLayoutParams(params);
        return view;
    }

    /**
     * Creates a color swatch.
     */
    private ColorPickerSwatch createColorSwatch(int colorPrimary, int colorPrimaryDark, int selectedColor) {
        ColorPickerSwatch view = new ColorPickerSwatch(getContext(), colorPrimary, colorPrimaryDark, colorPrimary == selectedColor, onColorSelectedListener);
        TableRow.LayoutParams params = new TableRow.LayoutParams(swatchLength, swatchLength);
        params.setMargins(marginSize, marginSize, marginSize, marginSize);
        view.setLayoutParams(params);
        return view;
    }
}