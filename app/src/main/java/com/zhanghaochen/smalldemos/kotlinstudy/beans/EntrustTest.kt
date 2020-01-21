package com.zhanghaochen.smalldemos.kotlinstudy.beans

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * @author created by zhanghaochen
 * @date 2020-01-21 13:21
 * 描述：委托模式的测试
 *
 * 在委托模式中，有两个对象参与处理同一个请求，接受请求的对象将请求委托给另一个对象来处理。
 * Kotlin 直接支持委托模式，更加优雅，简洁。Kotlin 通过关键字 by 实现委托。
 */
/**********************类委托********************/
// 类的委托即，一个类中定义的方法，实际是调用另一个类的对象的方法来实现的

// 创建接口
interface EntrustTestBase {
    fun print()
}

// 实现此接口的被委托的类
class EntrustTestBaseImpl(val x: Int) : EntrustTestBase {
    override fun print() {
        print(x)
    }
}

// 通过关键字by，建立委托类
class Derivedd(b: EntrustTestBase) : EntrustTestBase by b
// 在deriverdd声明中，by子句表示，将b保存在deriverdd的实例对象内部，
// 而且编译器将会生成继承自Base接口的所有方法，并将调用转发给b
/****************************************/

/***************属性委托*************************/
/**
 * 属性委托指的是一个类的某个属性值不是在类中直接进行定义，而是将其托付给一个代理类，从而实现对该类的属性统一管理。
属性委托语法格式：
val/var <属性名>: <类型> by <表达式>
var/val：属性类型(可变/只读)
属性名：属性名称
类型：属性的数据类型
表达式：委托代理类
by 关键字之后的表达式就是委托, 属性的 get() 方法(以及set() 方法)将被委托给这个对象的 getValue() 和 setValue() 方法。属性委托不必实现任何接口,
但必须提供 getValue() 函数(对于 var属性,还需要 setValue() 函数)。
 */
/**
 * 定义一个被委托的类
 * 该类需要包含 getValue() 方法和 setValue() 方法，且参数 thisRef 为进行委托的类的对象，prop 为进行委托的属性的对象
 */
class Example {
    var p: String by Delegatt()
}

class Delegatt {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, 这里委托了 ${property.name} 属性"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$thisRef 的 ${property.name} 属性赋值为$value")
    }
}
/****************************************/
/***************标准委托*********************/
//Kotlin 的标准库中已经内置了很多工厂方法来实现属性的委托。
/**
 * lazy() 是一个函数, 接受一个 Lambda 表达式作为参数, 返回一个 Lazy <T> 实例的函数，
 * 返回的实例可以作为实现延迟属性的委托：
 * 第一次调用 get() 会执行已传递给 lazy() 的 lamda 表达式并记录结果， 后续调用 get() 只是返回记录的结果。
 */
val lazyValue: String by lazy {
    // 第一次调用输出，第二次调用就不执行了
    println("computed!")
    "hello"
}

/**
 * not null
 * notNull 适用于那些无法在初始化阶段就确定属性值的场合
 */
class NotNullTest {
    // 使用的时候再赋值
    // 如果属性在赋值前就被访问的话则会抛出异常
    var notBullBar: String by Delegates.notNull<String>()
}
/****************************************/

/***************可观察属性Observable*********************/
/**
 * observable可以用于实现观察者模式。
 * Delegates.observable() 函数接受两个参数: 第一个是初始化值, 第二个是属性值变化事件的响应器(handler)。
 * 在属性赋值后会执行事件的响应器(handler)，它有三个参数：被赋值的属性、旧值和新值
 */
class ObTest {
    var name: String by Delegates.observable("初始值") { property, oldValue, newValue ->
        println("旧值：$oldValue -> 新值：$newValue")
    }
}

/****************************************/

/********************把属性储存在映射中****************/
/**
 * 一个常见的用例是在一个映射（map）里存储属性的值。 这经常出现在像解析 JSON 或者做其他"动态"事情的应用中。
 * 在这种情况下，你可以使用映射实例自身作为委托来实现委托属性。
 */
class RefectTest(val map: Map<String, Any?>) {
    /**
     * 如果使用 var 属性，需要把 Map 换成 MutableMap
     */
    val name: String by map
    val url: String by map
}

/*******************************************/

fun main(args: Array<String>) {
    val b = EntrustTestBaseImpl(10)
    Derivedd(b).print()

    val e = Example()
    println(e.p)// 访问该属性，调用getValue()函数
    e.p = "zhang"// 调用setvalue函数
    println(e.p)

    println(lazyValue)
    println(lazyValue)

    val ob = ObTest()
    ob.name = "第一次赋值"
    ob.name = "第二次赋值"

    // 构造函数接收一个映射参数
    val refect = RefectTest(mapOf(
        "name" to "菜鸟教程",
        "url" to "www.baidu.com"
    ))
    println(refect.name)
    println(refect.url)
    refect.map.forEach {
        println("key:${it.key},value:${it.value}")
    }
}
