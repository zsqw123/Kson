fun main() {
    kson(true) {
        "awa" - 1
        "b" - false
        "c" - "888"
        "o" - obj {
            "hhh" - 66
            "az" - null
            "mmp" - true
        }
        "e" - arr[
            1,
            null,
            nbj {
                "o" - 666
                "oo" - obj {
                    "ooo" - null
                }
                "222" - false
            },
            false
        ]
        "\"raw\":\"aaaa\"".raw()
        "d" - null
    }.prl()
}

fun Any.prl() = println(this)