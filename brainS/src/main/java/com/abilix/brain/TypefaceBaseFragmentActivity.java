package com.abilix.brain;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * creater: yhd
 * created: 2017/12/28 16:23
 */

public class TypefaceBaseFragmentActivity extends FragmentActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
