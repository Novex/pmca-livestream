package com.sony.imaging.lib.ustream;

class LibraryLoader {
    private static boolean initialized = false;

    LibraryLoader() {
    }

    static void init() {
        if (!initialized) {
            System.loadLibrary("UstreamTime");
            System.loadLibrary("UstreamSleep");
            System.loadLibrary("UstreamLib");
            initialized = true;
        }
    }
}
