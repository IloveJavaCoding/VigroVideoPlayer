package com.nepalese.virgovideoplayer.presentation.event;

/**
 * @author nepalese on 2020/11/24 10:38
 * @usage
 */
public class LocalVideoPlayEvent {
    private int curIndex;

    public LocalVideoPlayEvent(int curIndex) {
        this.curIndex = curIndex;
    }

    public int getCurIndex() {
        return curIndex;
    }
}
