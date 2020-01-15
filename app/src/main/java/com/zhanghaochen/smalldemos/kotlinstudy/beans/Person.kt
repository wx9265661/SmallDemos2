package com.zhanghaochen.smalldemos.kotlinstudy.beans

/**
 * @author created by zhanghaochen
 * @date 2020-01-13 14:38
 * 描述：
 */
class Person(name: String) {
    var url: String = "www.baidu.com"
    var country: String = "CN"
    var siteName = name

    /**
     * 类也可以有二级构造函数，需要加前缀 constructor:
     * 如果类有主构造函数，每个次构造函数都要，或直接或间接通过另一个次构造函数代理主构造函数。
     * 在同一个类中代理另一个构造函数使用 this 关键字
     */
    constructor() : this("") {}

    init {
        println("初始化url:$name")
    }

    fun printTest() {
        println("我是类函数")
    }

    // todo 这个field，我推测应该是指代的get和set中的对应变量，需要着重在了解
    // Kotlin 中类不能有字段。提供了 Backing Fields(后端变量) 机制,
    // 备用字段使用field关键字声明,field 关键词只能用于属性的访问器
    var lastName: String = "zhang"
        get() = field.toUpperCase()
        set

    var no: Int = 100
        get() {
            return field
        }
        set(value) {
            if (value < 10) {
                field = value
            } else {
                field = -1
            }
        }

    var heigh: Float = 134.22f
}

/**
 * 如果一个非抽象类没有声明构造函数(主构造函数或次构造函数)，它会产生一个没有参数的构造函数。
 * 构造函数是 public 。
 * 如果你不想你的类有公共的构造函数，你就得声明一个空的主构造函数
 */
class DontCreateMe private constructor() {
}

fun main(args: Array<String>) {
    var person: Person = Person()

    person.lastName = "wang"
    println("lastName:${person.lastName}")

    person.no = 9
    println("no:${person.no}")

    person.no = 20
    println("no:${person.no}")

    println("${person.heigh}")

    var person2: Person = Person("www.google.com")
    println(person2.siteName)
    println(person2.url)
    person2.printTest()
}


