/*
 * Copyright (c) 2023. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid;

import java.util.HashMap;

/**
 * This is a GUI placement grid whose size can be scaled by a given factor.
 * It also keeps the relative positions of GUI elements when scaled.
 */
public class ScaledGrid {
    public final HashMap<String, GridRectangle> rectangleMap;
    public final float originalXPosition;
    public final float originalYPosition;
    public final float originalGridWidth;
    public final float originalGridHeight;
    public final float originalGridUnitWidth;
    public final float originalGridUnitHeight;

    private float gridStartX;
    private float gridStartY;
    private float gridWidth;
    private float gridHeight;
    private float gridUnitWidth;
    private float gridUnitHeight;
    private float scaleFactor;
    private boolean scaled;
    /**
     * If {@code true}, multiply the position by the scale factor. If {@code false}, leave position as is.
     * This is currently unused (false by default).
     */
    protected boolean scaleStartPosition;
    /** If {@code true}, shift the start position so the grid looks like it's being expanded from the centre instead of the top left when scaled. If {@code false}, leave position as is. This works only when {@code scaleStartPosition} is {@code false}. */
    protected boolean centerStartPositionWhenScaled;

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, {@code numberOfRowsAndColumns} rows,
     * and {@code numberOfRowsAndColumns} columns.
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param numberOfRowsAndColumns number of rows and number of columns the grid will have
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, int numberOfRowsAndColumns, boolean centerStartPositionWhenScaled) {
        this(gridStartX, gridStartY, gridWidth, gridHeight, numberOfRowsAndColumns, numberOfRowsAndColumns, centerStartPositionWhenScaled);
    }

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, {@code numberOfRows} rows,
     * and {@code numberOfColumns} columns.
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param numberOfRows number of rows the grid will have
     * @param numberOfColumns number of columns the grid will have
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, int numberOfRows, int numberOfColumns, boolean centerStartPositionWhenScaled) {
        this(gridStartX, gridStartY, gridWidth, gridHeight, gridWidth / numberOfColumns, gridHeight / numberOfRows, centerStartPositionWhenScaled);
    }

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, and grid squares {@code gridUnitSize} by {@code gridUnitSize} in size
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param gridUnitSize side length of each grid square
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, float gridUnitSize, boolean centerStartPositionWhenScaled) {
        this(gridStartX, gridStartY, gridWidth, gridHeight, gridUnitSize, gridUnitSize, centerStartPositionWhenScaled);
    }

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, and grid rectangles {@code gridUnitWidth} by {@code gridUnitHeight} in size
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param gridUnitWidth width of each grid rectangle
     * @param gridUnitHeight height of each grid rectangle
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, float gridUnitWidth, float gridUnitHeight, boolean centerStartPositionWhenScaled) {
        this.rectangleMap = new HashMap<>();
        this.originalXPosition = gridStartX;
        this.originalYPosition = gridStartY;
        this.originalGridWidth = gridWidth;
        this.originalGridHeight = gridHeight;
        this.originalGridUnitWidth = gridUnitWidth;
        this.originalGridUnitHeight = gridUnitHeight;
        this.gridStartX = gridStartX;
        this.gridStartY = gridStartY;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gridUnitWidth = gridUnitWidth;
        this.gridUnitHeight = gridUnitHeight;
        this.scaleFactor = 1;
        this.scaled = false;
        this.centerStartPositionWhenScaled = centerStartPositionWhenScaled;
    }



    /**
     * Find the nearest grid x-coordinate to the given mouse x-position in pixels
     */
    public int findNearestGridX(int mouseX) {
        float offset = mouseX - this.gridStartX;
        float quotient = offset / this.gridUnitWidth;
        float remainder = offset / this.gridUnitWidth;

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > this.gridUnitWidth / 2 ? quotient + 1 : quotient);
    }

    public void addRectangle(String name, GridRectangle rectangle) {
        if (rectangle != null) {
            if (this.scaled) {
                rectangle.scale(this.scaleFactor);
            }

            this.rectangleMap.put(name, rectangle);
        } else {
            throw new NullPointerException("Cannot add a null GridRectangle to ScaledGrid");
        }
    }

    public void removeRectangle(String name) {
        this.rectangleMap.remove(name);
    }

    /**
     * Find the nearest grid y-coordinate to the given mouse y-position in pixels
     */
    public int findNearestGridY(int mouseY) {
        float offset = mouseY - this.gridStartY;
        float quotient =  offset / this.gridUnitHeight;
        float remainder = offset % this.gridUnitHeight;

        // Truncate instead of rounding to keep the point above the cursor
        return (int) (remainder > this.gridUnitHeight / 2 ? quotient + 1 : quotient);
    }

    /**
     * Find the x-position in pixels of a given grid x-coordinate
     */
    public float getActualX(int gridX) {
        return this.gridStartX + getOffsetX(gridX);
    }

    /**
     * Find the y-position in pixels of a given grid y-coordinate
     */
    public float getActualY(int gridY) {
        return this.gridStartY + getOffsetY(gridY);
    }

    /**
     * Get the offset of {@code gridX} from {@code gridStartX} in pixels
     */
    public float getOffsetX(float gridX) {
        return this.gridUnitWidth * gridX;
    }

    /**
     * Get the offset of {@code gridY} from {@code gridStartY} in pixels
     */
    public float getOffsetY(int gridY) {
        return this.gridUnitHeight * gridY;
    }

    public float getScaleFactor() {
        return this.scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        this.scaled = scaleFactor != 1;

        this.gridWidth = getScaledDimension(this.originalGridWidth);
        this.gridHeight = getScaledDimension(this.originalGridHeight);
        this.gridUnitWidth = getScaledDimension(this.originalGridUnitWidth);
        this.gridUnitHeight = getScaledDimension(this.originalGridUnitHeight);

        if (this.scaleStartPosition) {
            this.gridStartX = getScaledDimension(this.originalXPosition);
            this.gridStartY = getScaledDimension(this.originalYPosition);
        } else if (this.centerStartPositionWhenScaled) {
            this.gridStartX = this.originalXPosition - Math.abs((this.gridWidth - this.originalGridWidth)) / 2;
            this.gridStartY = this.originalYPosition - Math.abs((this.gridHeight - this.originalGridHeight)) / 2;
        }

        for (GridRectangle rectangle : this.rectangleMap.values()) {
            rectangle.scale(scaleFactor);
        }
    }

    public float getGridStartX() {
        return this.gridStartX;
    }

    public void setGridStartX(float gridStartX) {
        this.gridStartX = gridStartX;
    }

    public float getGridStartY() {
        return this.gridStartY;
    }

    public void setGridStartY(float gridStartY) {
        this.gridStartY = gridStartY;
    }

    public float getGridWidth() {
        return this.gridWidth;
    }

    public float getGridHeight() {
        return this.gridHeight;
    }

    public float getScaledDimension(float originalDimension) {
        if (this.scaled) {
            return originalDimension * this.scaleFactor;
        } else {
            return originalDimension;
        }
    }

    public boolean isScaled() {
        return this.scaled;
    }

    @Override
    public String toString() {
        return "ScaledGrid{" +
                "rectangleMap=" + this.rectangleMap +
                ", originalXPosition=" + this.originalXPosition +
                ", originalYPosition=" + this.originalYPosition +
                ", originalGridWidth=" + this.originalGridWidth +
                ", originalGridHeight=" + this.originalGridHeight +
                ", originalGridUnitWidth=" + this.originalGridUnitWidth +
                ", originalGridUnitHeight=" + this.originalGridUnitHeight +
                ", gridStartX=" + this.gridStartX +
                ", gridStartY=" + this.gridStartY +
                ", gridWidth=" + this.gridWidth +
                ", gridHeight=" + this.gridHeight +
                ", gridUnitWidth=" + this.gridUnitWidth +
                ", gridUnitHeight=" + this.gridUnitHeight +
                ", scaleFactor=" + this.scaleFactor +
                ", scaled=" + this.scaled +
                '}';
    }
}
