package org.example;

public class DummyUnit extends BaseUnit {
    private int clicks = 0;

    public DummyUnit(int id, Player owner, String name) {
        super(id, owner, name);
    }

    public int getClicks() {
        return clicks;
    }

    public void click() {
        clicks++;
    }

    @Override
    public String toString() {
        return "DummyUnit{" +
                "clicks=" + clicks +
                '}';
    }
}
