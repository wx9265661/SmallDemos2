package com.zhanghaochen.smalldemos.kotlinstudy.beans

/**
 * @author created by zhanghaochen
 * @date 2020-01-15 16:47
 * 描述：抽象类
 */
abstract class Base {
    abstract fun f(): Int?
    abstract fun a()
}

class Derived : Base() {
    override fun f() = 2

    override fun a() {
        println("重写方法")
    }

    private val bar: Int = 1

    class Nested {
        fun foo(): Int {
            return 2
        }
    }
}

/**
 * 内部类
 */
class Outer {
    private val bar: Int = 1
    private val v: String = "成员变量"

    // 内部类使用 inner 关键字来表示。
    // 内部类会带有一个对外部类的对象的引用，所以内部类可以访问外部类成员属性和成员函数
    inner class Inner {
        // 访问外部类成员
        fun foo() = bar

        fun innerTest() {
            // 获取外部类的成员变量
            // 要访问来自外部作用域的 this，我们使用this@label，其中 @label 是一个 代指 this 来源的标签
            var o = this@Outer
            println("内部类可以引用外部类的成员：${o.v}")
        }
    }
}

class Test {
    var v = "成员属性"
    var testInterface: TestInterface? = null
    fun setInterFace(test: TestInterface) {
        test.test()
    }

    fun test() {
        testInterface?.test() ?: println("回调为null")
    }
}

/**
 * 定义接口
 */
interface TestInterface {
    fun test()
}


fun main(args: Array<String>) {
    // 调用格式：外部类.嵌套类.嵌套类方法/属性
    val demo = Derived.Nested().foo()
    println(demo)

    // 内部类其实是外部类的一个成员变量，外部类需要加()
    // 要想构造内部类的对象，必须先构造外部类的对象，而嵌套类则不需要
    val demo2 = Outer().Inner().foo()
    println(demo2)
    println(Outer().Inner().innerTest())

    // 匿名内部类的测试
    var test = Test()
    // 采用对象表达式来创建接口对象，即匿名内部类的实例
    // 这个 object 是 Kotlin 的关键字，要实现匿名内部类，就必须使用 object 关键字，不能随意替换其它单词，切记切记
    test.setInterFace(object : TestInterface {
        override fun test() {
            println("对象表达式创建匿名内部类的实例")
        }
    })
    test.test()

    /**
     * 类属性修饰符
     * abstract    // 抽象类
     * final       // 类不可继承，默认属性
     * enum        // 枚举类
     * open        // 类可继承，类默认是final的
     * annotation  // 注解类
     *
     * 访问权限修饰符
     * private    // 仅在同一个文件中可见
     * protected  // 同一个文件中或子类可见
     * public     // 所有调用的地方都可见
     * internal   // 同一个模块中可见
     */
}
