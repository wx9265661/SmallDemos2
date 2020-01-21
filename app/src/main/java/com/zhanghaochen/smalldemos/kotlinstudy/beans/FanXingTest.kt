package com.zhanghaochen.smalldemos.kotlinstudy.beans

/**
 * @author created by zhanghaochen
 * @date 2020-01-20 10:51
 * 描述：泛型，即 "参数化类型"，将类型参数化，可以用在类，接口，方法上
 */
class Box<T>(t: T) {
    var value = t
}

/**
 * Kotlin 泛型函数的声明与 Java 相同，类型参数要放在函数名的前面
 */
fun <T> boxIn(value: T) = Box(value)

fun <T> doPrintln(content: T) {
    when (content) {
        is Int ->
            println("整型数字为 $content")
        is String ->
            println("字符串转换为大写：${content.toUpperCase()}")
        else ->
            println("T 既不是整型，也不是字符串型")
    }
}

/*******************泛型约束****************/
/**
 * 我们可以使用泛型约束来设定一个给定参数允许使用的类型。
 * Kotlin 中使用 : 对泛型的类型上限进行约束
 */
fun <T : Comparable<T>> sort(list: List<T>) {

}
//Comparable 的子类型可以替代 T
//sort(listOf(1, 2, 3)) // OK。Int 是 Comparable<Int> 的子类型
//sort(listOf(HashMap<Int, String>())) // 错误：HashMap<Int, String> 不是 Comparable<HashMap<Int, String>> 的子类型
/**
 * 对于多个上界约束条件，可以用 where 子句
 */
fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String>
    where T : CharSequence,
          T : Comparable<T> {
    return list.filter {
        it > threshold
    }.map {
        it.toString()
    }
}
/************************************************/

/*******************型变*******************/
/**
 * 声明处的类型变异使用协变注解修饰符：in、out，消费者 in, 生产者 out。
 * 使用 out 使得一个类型参数协变，协变类型参数只能用作输出，可以作为返回值类型但是无法作为入参的类型。
 */
class Runoob<out B>(val a: B) {
    fun foo(): B {
        return a
    }
}

class Runoob2<in A>(a: A) {
    fun foo(a: A) {

    }
}
/**************************************/

/**
 * 星号投射
 * 其实就是*代指了所有类型，相当于Any?
 */
class Star<T>(val t: T, val t2: T, val t3: T)

class Apple(var name: String)

fun main(args: Array<String>) {
    // 创建类的实例时我们需要指定参数类型
    val box: Box<Int> = Box(1)
    // 编译器会进行类型推断，1 类型 Int，所以编译器知道我们说的是 Box<Int>
    val box2 = Box(2)

    val box3 = Box("noob")

    val box4 = boxIn(1)

    println(box2.value)
    println(box3.value)

    val age = 23
    val name = "runoob"
    val bool = true

    doPrintln(age)    // 整型
    doPrintln(name)   // 字符串
    doPrintln(bool)   // 布尔型

    // 星号投射测试
    val a1: Star<*> = Star(12, "String", Apple("苹果"))
    // 和a1是一样的
    val a2: Star<Any?> = Star(12, "String", Apple("苹果"))
    val apple = a1.t3
    println(apple)
    val apple2 = apple as Apple//强转成Apple类
    println(apple2.name)
}
