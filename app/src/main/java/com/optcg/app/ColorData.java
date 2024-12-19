package com.optcg.app;

import java.util.List;

public class ColorData {
    private final String title;
    private final List<Integer> drawables;

    public ColorData(String title, List<Integer> drawables) {
        this.title = title;
        this.drawables = drawables;
    }

    public String getTitle() {
        return title;
    }

    public List<Integer> getDrawables() {
        return drawables;
    }
}
