package com.magicianguo.settingtools.util;

import moe.shizuku.server.IShizukuService;
import rikka.shizuku.Shizuku;

public class ShizukuImpl extends Shizuku {
    public static IShizukuService getService() {
        return requireService();
    }
}
