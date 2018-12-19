package com.sample.systrace;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Trace;



public class TraceTag {

    private static final String TAG = "TraceTag";

    /**
     * hook method when it's called in.
     *
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void i(String name) {
        Trace.beginSection(name);
    }

    /**
     * hook method when it's called out.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void o() {
        Trace.endSection();
    }
}
