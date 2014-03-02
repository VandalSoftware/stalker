package com.vandalsoftware.example.single.androidlib;

import android.content.Context;

public interface Dictionary {

    void init(Context context);
    boolean save(Definition d);
    boolean delete(Definition d);
    boolean deleteAll();
    void close();
}
