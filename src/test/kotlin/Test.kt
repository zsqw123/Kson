fun main() {
    obj(true) {
        "awa" - 1
        "b" - false
        "c" - "888"
        "o" - obj {
            "hhh" - 66
            "az" - null
            "mmp" - false
        }
        "e" - arr[
            1,
            null,
            obj {
                "套娃" - 666
                "222" - false
            },
            false
        ]
        "\"raw\":\"aaaa\"".raw()
        "d" - null
    }.prl()
}

fun Any.prl() = println(this)