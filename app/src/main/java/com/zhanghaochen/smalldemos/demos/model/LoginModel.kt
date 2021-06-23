package com.zhanghaochen.smalldemos.demos.model

import android.content.Context
import android.widget.Toast
import androidx.databinding.ObservableField

/**
 * @author created by zhanghaochen
 * @date 2021-06-21 4:30 PM
 * 描述：登录model，赋值登录逻辑和输入框内容更新处理
 */
class LoginModel constructor(name: String, pwd: String, context: Context) {
    val n = ObservableField<String>(name)
    val p = ObservableField<String>(pwd)
    var context = context

    fun onNameChanged(s: CharSequence) {
        n.set(s.toString())
    }

    fun onPwdChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        p.set(s.toString())
    }

    fun login() {
        if (n.get() == "admin" && p.get() == "admin") {
            Toast.makeText(context, "账号密码正确", Toast.LENGTH_SHORT).show()
        }
    }
}