package com.zhanghaochen.smalldemos.kotlinstudy.beans

/**
 * @author created by zhanghaochen
 * @date 2020-01-21 10:46
 * 描述：
 */
/*******************对象表达式和对象声明*****************/
/**
 *对象表达式
 *通过对象表达式实现一个匿名内部类的对象用于方法的参数中
 */
open class AA(x: Int) {
    public open val y: Int = x
}

interface BB {

}

// 对象可以继承于某个基类，或者实现其他接口
// 如果超类型有一个构造函数，则必须传递参数给它。多个超类型和接口可以用逗号分隔。
val ab: AA = object : AA(1), BB {
    override val y: Int
        get() = 15
}

/**
 * 使用object关键字来声明一个对象
 * 通过这种方式，我们获得一个单例
 */
object Site {
    var url: String = ""
    val name: String = "zhang"
}

/**
 * 与对象表达式不同，当对象声明在另一个类的内部时，这个对象并不能通过外部类的实例访问到该对象，
 * 而只能通过类名来访问，同样该对象也不能直接访问到外部类的方法和变量。
 */
class Site2 {
    var name = "菜鸟教程"

    object DeskTop {
        var url = "www.runoob.com"
        fun showName() {
            //print{"desk legs $name"} // 错误，不能访问到外部类的方法和变量
        }
    }
}

/**
 * 类内部的对象声明可以用 companion 关键字标记，
 * 这样它就与外部类关联在一起，我们就可以直接通过外部类访问到对象的内部元素
 *
 * 一个类里面只能声明一个内部关联对象，即关键字 companion 只能使用一次
 */
class MyClasses {
    companion object Factory {
        fun create(): MyClasses {
            return MyClasses()
        }
    }

    var ss = "单例测试"
}

/**
 * 常规的懒汉式
 * 单例模式
 */
class SingletonDemo2 private constructor() {
    companion object {
        private var instance: SingletonDemo2? = null
            get() {
                if (field == null) {
                    field = SingletonDemo2()
                }
                return field
            }

        fun get(): SingletonDemo2 {
            //细心的小伙伴肯定发现了，这里不用getInstance作为为方法名，是因为在伴生对象声明时，内部已有getInstance方法，所以只能取其他名字
            return instance!!
        }
    }
}

/**
 * 双重校验锁式
 * 单例模式
 */
class SingletonDemo private constructor() {
    companion object {
        val instance: SingletonDemo by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SingletonDemo()
        }
    }
}

fun main(args: Array<String>) {
    // 通过对象表达式可以越过类的定义直接得到一个对象
    var site = object {
        var name: String = "测试测试"
        var url: String = "www.baidu.com"
    }

    println(site.name)
    println(site.url)

    var s1 = Site
    var s2 = Site
    s1.url = "www.dd.com"
    println(s2.url)

    var site2 = Site2()
    // site2.DeskTop.url // 错误，不能通过外部类的实例访问到该对象
    Site2.DeskTop.url // 正确/

    val instance = MyClasses.create()
    instance.ss = "单例222"
    val instance2 = MyClasses.create()
    println(instance2.ss)

    SingletonDemo.instance
    SingletonDemo2.get()
}
