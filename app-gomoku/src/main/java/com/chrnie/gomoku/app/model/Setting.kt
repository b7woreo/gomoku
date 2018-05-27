package com.chrnie.gomoku.app.model

import android.content.Context
import com.chrnie.gomoku.app.const.DEFAULTY_DIFFICALTY
import com.chrnie.gomoku.app.const.KEY_SETTING_DIFFICALTY
import com.chrnie.gomoku.app.const.SP_NAME_SETTING

class Setting(context: Context) {

    private val sp by lazy { context.getSharedPreferences(SP_NAME_SETTING, Context.MODE_PRIVATE) }

    var difficalty: Int
        get() = sp.getInt(KEY_SETTING_DIFFICALTY, DEFAULTY_DIFFICALTY)
        set(value) = sp.edit().putInt(KEY_SETTING_DIFFICALTY, value).apply()
}