package com.example.q.cs496_app1.tabs.third;

public class Tile extends Cell {
    private final int value;
    private Tile[] mergedFrom = null;

    public Tile(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public Tile(Cell cell, int value) {
        super(cell.getX(), cell.getY());
        this.value = value;
    }

    public void updatePosition(Cell cell) {
        this.setX(cell.getX());
        this.setY(cell.getY());
    }

    public int getValue() {
        return value;
    }

    public Tile[] getMergedFrom() {
        return mergedFrom;
    }

    public void setMergedFrom(Tile[] mergedForm) {
        this.mergedFrom = mergedForm;
    }
}
