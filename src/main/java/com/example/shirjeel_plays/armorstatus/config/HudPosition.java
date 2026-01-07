package com.example.shirjeel_plays.armorstatus.config;

public enum HudPosition {
    TOP_LEFT("Top Left"),
    MIDDLE_LEFT("Middle Left"),
    BOTTOM_LEFT("Bottom Left"),
    TOP_RIGHT("Top Right"),
    MIDDLE_RIGHT("Middle Right"),
    BOTTOM_RIGHT("Bottom Right");

    private final String name;

    HudPosition(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public HudPosition next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
