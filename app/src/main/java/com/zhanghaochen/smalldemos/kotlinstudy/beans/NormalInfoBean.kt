package com.zhanghaochen.smalldemos.kotlinstudy.beans

import java.lang.IllegalArgumentException
import kotlin.experimental.and

/**
 * @author created by zhanghaochen
 * @date 2020-01-09 2:43 PM
 * 描述：
 */
class NormalInfoBean {

    class NormalInfoBean constructor(name: String) {}

    // kotlin中没有基础数据类型，只有封装的数字类型，你每定义的一个变量，其实 Kotlin 帮你封装了一个对象，这样可以保证不会出现空指针
    // 所以在比较两个数字的时候，就有比较数据大小和比较两个对象是否相同的区别了
    // 在 Kotlin 中，三个等号 === 表示比较对象地址，两个 == 表示比较两个值大小

    /**
     * 以使用下划线使数字常量更易读
     */
    val oneMillion: Int = 1_000_000
    val creditCardNumer = 1234_5678_333L
    /**
     * 16 进制以 0x 开头
     */
    val hexBytes = 0xFF_dd_ff
    /**
     * 2 进制以 0b 开头
     */
    val bytes = 0b000111

    private fun getString(name: String, value: String): String {

        return name + value
    }

    /**
     * 表达式作为函数体，返回类型自动推断（public 方法则必须明确写出返回类型）
     */
    private fun getString2(name: String, value: String) = name + value

    /**
     * unit类型就是java的void
     */
    fun printSum(a: Int, b: Int): Unit {
        print(a + b)
    }

    /**
     * 如果是返回 Unit类型，则可以省略(对于public方法也是这样)：
     */
    fun printSum2(a: Int, b: Int) {
        print(a + b)
    }

    /**
     * 变长参数可以用 vararg 关键字进行标识
     */
    public fun vars(vararg values: Int): Unit {
        for (value in values) {
            print(value)
        }
    }

    /**
     * lambda(匿名函数)测试
     */
    private fun testLambda() {
        val sumLambda: (Int, Int, Int) -> Int = { x, y, z ->
            x + y + 2 * z
        }
        print(sumLambda(3, 2, 4))
    }

    var a: Int = 3

    val b: Int = 3

    var c: Int = 5

    val str: String = "final"

    /**
     * 可变变量定义：var 关键字
     * 不可变变量定义：val 关键字
     */
    fun test01() {
        c = a + b
        print(s1)
    }

    /**
     * $ 表示一个变量名或者变量值
     * $varName 表示变量值
     */
    var s1 = "a is $a"

    /**
     * 字符串模板
     * ${varName.fun()} 表示变量的方法返回值
     */
    fun test02() {
        a = 66
        val s2 = "${s1.replace("is", "was")},but now is $a"
        print(s2)
    }

    //类型后面加?表示可为空
    var age: String? = "23"
    // age为空，抛出空指针异常
    val ages = age!!.toInt()
    //不做处理返回 null
    val ages1 = age?.toInt()
    //age为空返回-1
    val ages2 = age?.toInt() ?: -1

    /**
     * 当 str 中的字符串内容不是一个整数时, 返回 null:
     */
    fun parseInt(str: String?): Int? {
        return str?.toInt()
    }

    /**
     * 可以使用 is 运算符检测一个表达式是否某类型的一个实例(类似于Java中的instanceof关键字)
     */
    fun getStringLength(obj: Any): Int? {
        if (obj is String) {
            // 做过类型判断后，会自动转换成String类型
            return obj.length
        }

        //在这里还有一种方法，与Java中instanceof不同，使用!is
        // if (obj !is String){
        //   // XXX
        // }

        return null
    }

    fun getStringLength2(obj: Any): Int? {
        if (obj !is String) {
            return null
        }
        // 在这个分支中，`obj` 的类型会被自动转换为 `String`
        return obj.length
    }

    fun getStringLength3(obj: Any): Int? {
        // 在 `&&` 运算符的右侧, `obj` 的类型会被自动转换为 `String`
        if (obj is String && obj.length > 0) {
            return obj.length
        }
        return null
    }

    /**
     * 区间表达式由具有操作符形式 .. 的 rangeTo 函数辅以 in 和 !in 形成。
     * 区间是为任何可比较类型定义的，但对于整型原生类型，它有一个优化的实现
     */
    fun test03() {
        for (i in 1..4) {
            // 输出'1234'
            print(i)
        }

        for (i in 4..1) {
            // 没输出
            print(i)
        }

        for (i in 1..4 step 2) {
            // 使用step指定步长，输出'13'
            print(i)
        }

        for (i in 4 downTo 1 step 2) {
            // 输出'42'
            print(i)
        }

        for (i in 1 until 4) {
            // i in [1,4) 排除了4
            // 输出'123
            print(i)
        }
    }

    /**
     * ===和==的区别
     */
    fun test04() {
        val a: Int = 1000
        // true，值相等，对象地址相等
        println(a === a)

        // 经过装箱，创建了两个不同的对象
        val boxedA: Int? = a
        val anotherA: Int? = a
        // false，地址不同
        println(boxedA === anotherA)
        // true，值相等
        println(boxedA == anotherA)

    }

    /**
     * 和 Java 不一样，Kotlin 中的 Char 不能直接和数字操作，
     * Char 必需是单引号 ' 包含起来的。比如普通字符 '0'，'a'。
     *
     * 字符字面值用单引号括起来: '1'。 特殊字符可以用反斜杠转义。
     * 支持这几个转义序列：\t、 \b、\n、\r、\'、\"、\\ 和 \$。
     * 编码其他字符要用 Unicode 转义序列语法：'\uFF00'。
     */
    fun check(c: Char): Int? {
        if (c !in '0'..'9') {
            throw IllegalArgumentException("Out of range")
        }
        return c.toInt() - '0'.toInt()
    }

    /**
     * 数组用类 Array 实现，并且还有一个 size 属性及 get 和 set 方法，
     * 由于使用 [] 重载了 get 和 set 方法，所以我们可以通过下标很方便的获取或者设置数组对应位置的值。
     * 数组的创建两种方式：一种是使用函数arrayOf()；另外一种是使用工厂函数。
     */
    fun testArray() {
        // [1,2,3]
        val a = arrayOf(1, 2, 3)
        // [0,2,4]
        val b = Array(3) { i -> (i * 2) }

        println(a[1])
        println(b[2])

        val x = intArrayOf(3, 4, 5)
        x[0] = x[2] + x[1]

        var f = if (x[0] > x[1]) {
            x[0]
        } else {
            x[1]
        }

    }

    fun testString() {
        // Kotlin 支持三个引号 """ 扩起来的字符串，支持多行字符串
        var text = """
            多行字符串
            多行字符串
            多行字符串
        """.trimIndent()

        // 默认 | 用作边界前缀，但你可以选择其他字符并作为参数传入，比如 trimMargin(">")
        var text2 = """
            |多行字符串
            |菜鸟教程
            |多行字符串
            |Runoob
        """.trimMargin()
    }

    fun testWhen(x: Int?, y: Any) {
        // 在 when 中，else 同 switch 的 default。
        // 如果其他分支都不满足条件将会求值 else 分支。
        //如果很多分支需要用相同的方式处理，则可以把多个分支条件放在一起，用逗号分隔
        when (x) {
            1 ->
                print("x ==1")
            2 ->
                print("x==2")
            3, 4 ->
                print("3或者4")
            else -> {
                print("既不是1也不是2")
            }
        }

        // 也可以检测一个值在（in）或者不在（!in）一个区间或者集合中
        when (x) {
            in 1..10
            -> print("x is in range")
            !in 10..20
            -> print("x is out the range")
            else
            -> print("none of above")
        }

        // 检测一个值是（is）或者不是（!is）一个特定类型的值
        when (y) {
            is String ->
                y.startsWith("dd")
            else ->
                false
        }
    }

    fun testFor() {
        // for 循环可以对任何提供迭代器（iterator）的对象进行遍历
        var collection = intArrayOf(5, 4, 2)
        for (i in collection) {
            print(i)
        }

        var strs = arrayListOf<String>()
        for (i in 1..10) {
            strs.add("$i")
        }

        for (string: String in strs) {
            print(string)
        }

        // 通过索引遍历一个数组或者一个 list
        for (i in collection.indices) {
            print(collection[i])
        }

        // 可以用库函数 withIndex
        for ((index, value) in collection.withIndex()) {
            print("the element at $index is $value")
        }
    }

    fun testShile() {
        // do…while 循环 对于 while 语句而言，如果不满足条件，则不能进入循环。但有时候我们需要即使不满足条件，也至少执行一次。
        // do…while 循环和 while 循环相似，不同的是，do…while 循环至少会执行一次。
        println("---while 使用---")
        var x = 5
        while (x > 0) {
            println(x--)
        }

        var y = 5
        do {
            println(y--)
        } while (y > 0)

        // 在 Kotlin 中任何表达式都可以用标签（label）来标记。 标签的格式为标识符后跟 @ 符号，例如：abc@、fooBar@都是有效的标签。
        looptest@ for (i in 1..100) {
            for (j in 1..100) {
                if (j == 50) {
                    break@looptest
                }
            }
        }
    }
}
