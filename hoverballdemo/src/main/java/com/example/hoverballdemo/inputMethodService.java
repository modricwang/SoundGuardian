package com.example.hoverballdemo;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.inputmethod.InputConnection;

/**
 * Created by modri on 2017/3/8.
 */
public class inputMethodService extends InputMethodService {
    public void input(String s) {

        InputConnection ic=null;
        while (ic==null) {
            ic = getCurrentInputConnection();
            Log.e("ic", String.valueOf(ic == null));
        }
        ic.commitText(s,0);
    }
}