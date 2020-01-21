package com.zhanghaochen.smalldemos.kotlinstudy.beans

/**
 * @author created by zhanghaochen
 * @date 2020-01-20 16:54
 * 描述：枚举类练习
 *
 * 枚举类最基本的用法是实现一个类型安全的枚举。
 * 枚举常量用逗号分隔,每个枚举常量都是一个对象。
 */
enum class Color3 {
    RED, BLACK, BLUE, GREEN, WHITE
}

// 枚举初始化
// 每一个枚举都是枚举类的实例，他们可以被初始化
enum class Color2(var rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}

/**
 * 枚举还支持以声明自己的匿名类及相应的方法、以及覆盖基类的方法。
 * 如果枚举类定义任何成员，要使用分号将成员定义中的枚举常量定义分隔开
 */
enum class ProtocolState {
    WAITING {
        override fun signal(): ProtocolState {
            return TALKING
        }
    },
    TALKING {
        override fun signal(): ProtocolState {
            return WAITING
        }
    };

    abstract fun signal(): ProtocolState
}


enum class Color {
    // EnumClass.valueOf(value: String): EnumClass  // 转换指定 name 为枚举值，若未匹配成功，会抛出IllegalArgumentException
    // EnumClass.values(): Array<EnumClass>        // 以数组的形式，返回枚举值
    // val name: String //获取枚举名称
    // val ordinal: Int //获取枚举值在所有枚举数组中定义的顺序
    RED,
    BLACK, BLUE, GREEN, WHITE
}

/**
 * 可以使用 enumValues<T>() 和 enumValueOf<T>() 函数以泛型的方式访问枚举类中的常量
 */
enum class RGB { RED, GREEN, BLUE }

inline fun <reified T : Enum<T>> printAllValues() {
    print(enumValues<T>().joinToString { it.name })
}

fun main(args: Array<String>) {
    var color: Color = Color.BLUE

    println(Color.values())
    println(Color.valueOf("RED"))
    println(color.name)
    println(color.ordinal)

    printAllValues<RGB>() // 输出 RED, GREEN, BLUE

}
