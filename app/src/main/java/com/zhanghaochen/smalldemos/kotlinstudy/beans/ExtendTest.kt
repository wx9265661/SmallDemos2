package com.zhanghaochen.smalldemos.kotlinstudy.beans

/**
 * @author created by zhanghaochen
 * @date 2020-01-16 09:58
 * 描述：
 */

/**
 * Kotlin 中所有类都继承该 Any 类
 * Any 默认提供了三个函数
 * 如果一个类要被继承，可以使用 open 关键字进行修饰
 */
open class People constructor(var name: String, var age: Int) {
    init {
        println("初始化")
    }

    constructor(name: String, age: Int, no: String) : this(name, age) {
        // 初始化
        println("--------基类的次级构造函数-------")
    }

    /**
     * 在基类中，使用fun声明函数时，此函数默认为final修饰，不能被子类重写。
     * 如果允许子类重写该函数，那么就要手动添加 open 修饰它
     */
    open fun study() {
        println("我是个人")
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

/**
 * 如果子类有主构造函数， 则基类必须在主构造函数中立即初始化
 */
class Student constructor(name: String, age: Int, var no: String, var score: Int) : People(name, age, no) {
    /**
     * 子类重写方法使用 override 关键词
     */
    override fun study() {
        println("我是学生")
    }
}

/**
 * 如果子类没有主构造函数，则必须在每一个二级构造函数中用 super 关键字初始化基类，或者在代理另一个构造函数。
 * 初始化基类时，可以调用基类的不同构造方法。
 */
class Student2 : People {
    constructor(name: String, age: Int, no: String, score: Int) : super(name, age) {
        println("----------继承类的次级构造函数----------")
        println("名字:${name}")
        println("年龄:${age}")
        println("编号:${no}")
        println("成绩:${score}")
    }
}

open class A {
    open fun f() {
        println("A")
    }

    fun a() {
        println("a")
    }
}

/**
 * Kotlin 接口与 Java 8 类似，使用 interface 关键字定义接口，允许方法有默认实现
 *
 * 接口中的属性只能是抽象的，不允许初始化值，接口不会保存属性值，实现接口时，必须重写属性
 */
interface B {
    var name: String //name 属性，抽象的
    fun f() {
        print("B")
    }

    fun b() {
        print("b")
    }

    /**
     * 未实现的方法，没有方法体，是抽象的，实现接口时必须重写
     * 对于上面已经实现的方法，不需要强制重写
     */
    fun c()
}

/**
 * 如果有多个相同的方法（继承或者实现自其他类，如A、B类），
 * 则必须要重写该方法，使用super范型去选择性地调用父类的实现。
 *
 * C 继承自 a() 或 b(), C 不仅可以从 A 或则 B 中继承函数，而且 C 可以继承 A()、B() 中共有的函数。此时该函数在中只有一个实现，
 * 为了消除歧义，该函数必须调用A()和B()中该函数的实现，并提供自己的实现
 */
class C : A(), B {
    override var name: String = "nuub"

    override fun f() {
        super<A>.f()
        super<B>.f()
    }

    override fun c() {

    }
}

/**
 * 属性重写使用 override 关键字，属性必须具有兼容类型，
 * 每一个声明的属性都可以通过初始化程序或者getter方法被重写
 *
 * 可以用一个var属性重写一个val属性，但是反过来不行。
 * 因为val属性本身定义了getter方法，重写为var属性会在衍生类中额外声明一个setter方法
 */
interface Foo {
    val count: Int
}

/**
 * 可以在主构造函数中使用 override 关键字作为属性声明的一部分
 */
class Bar1(override val count: Int) : Foo

class Bar2 : Foo {
    override var count: Int = 0
}

fun main(args: Array<String>) {
    val s = Student("zhang", 18, "20", 100)
    println("名字:${s.name}")
    println("年龄:${s.age}")
    println("编号:${s.no}")
    println("成绩:${s.score}")
    s.study()

    var s2 = Student2("xing", 20, "30", 0)

    var c = C()
    c.f()
}
