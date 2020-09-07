package com.legatotechnologies.updater;

/**
 * Created by davidng on 11/6/17.
 */

public enum Language {
    Eng("New Update Available", "Update Later", "Update", "Skip"),
    Chinese_Trad ("更新公告", "稍後更新", "更新", "跳過"),
    Chinese_Simp ("更新公告", "稍后更新", "更新", "跳过");

    private final String title;
    private final String netural_btn;
    private final String pos_btn;
    private final String neg_btn;

    Language(String title, String netural_btn, String pos_btn, String neg_btn) {
        this.title = title;
        this.netural_btn = netural_btn;
        this.pos_btn = pos_btn;
        this.neg_btn = neg_btn;
    }

    public String getTitle() {
        return title;
    }

    public String getNeutral_btn() {
        return netural_btn;
    }

    public String getPos_btn() {
        return pos_btn;
    }

    public String getNeg_btn() {
        return neg_btn;
    }
}
