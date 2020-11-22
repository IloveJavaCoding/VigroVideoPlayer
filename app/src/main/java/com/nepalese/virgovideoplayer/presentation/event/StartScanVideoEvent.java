package com.nepalese.virgovideoplayer.presentation.event;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/11/20 08:56
 * @usage
 */
public class StartScanVideoEvent {
    private List<File> list;

    public StartScanVideoEvent(List<File> list) {
        this.list = list;
    }

    public List<File> getList() {
        return list;
    }
}
