package com.zhanghaochen.smalldemos.kotlinstudy.beans

/**
 * @author created by zhanghaochen
 * @date 2020-01-17 14:30
 * 描述：拓展函数的练习
 *
 * 扩展函数可以在已有类中添加新的方法，不会对原类做修改，扩展函数定义形式
 *
 * 通常扩展函数或属性定义在顶级包下
 * 使用所定义包之外的一个扩展, 通过import导入扩展的函数名进行使用
 *
 * fun receiverType.functionName(params){
 *   body
 *  }
 * receiverType：表示函数的接收者，也就是函数扩展的对象
 * functionName：扩展函数的名称
 * params：扩展函数的参数，可以为NULL
 */
class User(var name: String)

/**
 * 拓展函数
 */
fun User.Print() {
    println("用户名 $name")
}

/**
 * 扩展函数 swap,调换不同位置的值
 */
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    // this关键字指代接收者对象(receiver object)(也就是调用扩展函数时, 在点号之前指定的对象实例)
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}

/** ---------------------------------*/
/**
 * 扩展函数是静态解析的
 *
 * 扩展函数是静态解析的，并不是接收者类型的虚拟成员，在调用扩展函数时，
 * 具体被调用的的是哪一个函数，由调用函数的的对象表达式来决定的，而不是动态的类型决定的
 *
 * 若扩展函数和成员函数一致，则使用该函数时，会优先使用成员函数
 */
open class Expand01

class ExpandChild : Expand01()

fun Expand01.foo() = "Expand01"

fun ExpandChild.foo() = "ExpandChild"

fun printFoo(expand01: Expand01) {
    // 类型是Expand01
    println(expand01.foo())
}

/** ---------------------------------*/

/**
 * 在扩展函数内， 可以通过 this 来判断接收者是否为 NULL,这样，即使接收者为 NULL,也可以调用扩展函数。
 */
fun Any?.toString(): String {
    if (this == null) {
        return "null 0"
    }
    // 空检测之后，“this”会自动转换为非空类型，所以下面的 toString()
    // 解析为 Any 类的成员函数
    return toString()
}

/**
 * 除了函数，Kotlin 也支持属性对属性进行扩展:
 */
class ExpandMemberTest {
    /**
     * 扩展属性允许定义在类或者kotlin文件中，不允许定义在函数中。
     * 初始化属性因为属性没有后端字段（backing field），所以不允许被初始化，只能由显式提供的 getter/setter 定义
     */
    val <T>List<T>.lastIndex: Int
        get() = size - 1

    // val <T>List<T>.startIndex = 0 错误：拓展属性不能有初始化器，只能有get和set

    val list = listOf(1, 2, 3, 4, 5)

    fun lastIndex() {
        println("${list.lastIndex}")
    }
}

/**************************伴生对象的拓展****************************/
class MyClass {
    // 将被成为"Companion"
    companion object {}
}

fun MyClass.Companion.foo() {
    println("伴生对象的拓展函数")
}

val MyClass.Companion.no: Int
    get() = 10

/***********************************************************/

/***********************拓展声明为成员************************************/
/**
 * 在一个类内部你可以为另一个类声明扩展
 * 在这个扩展中，有个多个隐含的接受者，其中扩展方法定义所在类的实例称为分发接受者，
 * 而扩展方法的目标类型的实例称为扩展接受者
 */
class D {
    fun bar() {
        println("D bar")
    }
}

class Z {
    fun baz() {
        println("C baz")
    }

    fun D.foo() {
        // 调用D.bar
        bar()
        // 调用Z.baz
        baz()
    }

    fun caller(d: D) {
        // 调用拓展函数
        d.foo()
    }
}

/***********************************************************/
fun main(args: Array<String>) {
    val user = User("zhang")
    user.Print()

    val l = mutableListOf(1, 2, 3, 4, 5)
    // 位置0和2的值做了互换
    // 'swap()' 函数内的 'this' 将指向 'l' 的值
    l.swap(0, 2)
    println(l.toString())

    /**
     * 此处输出的是Expand01，并不是ExpandChild
     */
    printFoo(ExpandChild())

    var test = null
    println(test.toString())

    val memberTest = ExpandMemberTest()
    memberTest.lastIndex()

    println("no:${MyClass.no}")
    MyClass.foo()

    val d: D = D()
    val z: Z = Z()
    z.caller(d)
}


