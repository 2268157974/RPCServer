package com.lch.rpc.util;

public class Logg {
    private Logg() {
    }

    public static void d(String tag, String msg) {
        System.out.println(tag + " " + msg.toUpperCase());
    }
}
