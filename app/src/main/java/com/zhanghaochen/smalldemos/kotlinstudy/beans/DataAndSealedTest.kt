package com.zhanghaochen.smalldemos.kotlinstudy.beans

import android.opengl.Visibility
import android.view.View

/**
 * @author created by zhanghaochen
 * @date 2020-01-17 17:19
 * 描述：数据类与密封类
 *
 * Kotlin 可以创建一个只包含数据的类，关键字为 data
 *
 * 为了保证生成代码的一致性以及有意义，数据类需要满足以下条件：
 * 主构造函数至少包含一个参数。
 * 所有的主构造函数的参数必须标识为val 或者 var ;
 * 数据类不可以声明为 abstract, open, sealed 或者 inner;
 * 数据类不能继承其他类 (但是可以实现接口)。
 */
data class UserTest(val name: String, val age: Int)


/**
 * 解密封类就是一种专门用来配合 when 语句使用的类，举个例子，
 * 假如在 Android 中我们有一个 view，我们现在想通过 when 语句设置针对 view 进行两种操作：显示和隐藏
 *
 * 以上功能其实完全可以用枚举实现，但是如果我们现在想加两个操作：水平平移和纵向平移，
 * 并且还要携带一些数据，比如平移了多少距离，平移过程的动画类型等数据，
 * 用枚举显然就不太好办了，这时密封类的优势就可以发挥了
 */
sealed class UiOption {
    object Show : UiOption()
    object Hide : UiOption()
    class TranslateX(val px: Float) : UiOption()
    class TranslateY(val px: Float) : UiOption()
}

fun execute(view: View, option: UiOption) {
    when (option) {
        UiOption.Show ->
            view.visibility = View.VISIBLE
        UiOption.Hide ->
            view.visibility = View.GONE
        is UiOption.TranslateX ->
            view.translationX = option.px
        is UiOption.TranslateY ->
            view.translationY = option.px
    }
}

// 先封装一个UI操作列表
class Ui(val uiOps: List<UiOption> = emptyList()) {
    /**
     * operator关键修饰符：
     * 创建一个方法，方法名为保留的操作符关键字，这样就可以让这个操作符的行为映射到这个方法
     */
    operator fun plus(uiOption: UiOption) = Ui(uiOps + uiOption)
}

// 定义一组操作
val ui = Ui().plus(UiOption.Show) +
        UiOption.TranslateY(20f) +
        UiOption.TranslateY(30f) +
        UiOption.Hide

// 定义调用的函数
fun run(view: View, ui: Ui) {
    ui.uiOps.forEach {
        execute(view, it)
    }
}


fun main(args: Array<String>) {
    val jack = UserTest("Jack", 1)
    val olderJacker = jack.copy(age = 2)
    println(jack)
    println(olderJacker)

    /**
     * 组件函数允许数据类在解构声明中使用
     */
    val (name, age) = jack
    println("$name,$age years old")
}
