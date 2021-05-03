fun main() {
    val another = arr[
        1,
        null,
        arr[
            1, 2,
            arr[1, 2,
                obj {
                    "awa" - 1
                }
            ],
        ],
        obj {
            "awa" - 1
        }
    ]
    obj(true) {
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
            obj {
                "o" - obj {
                    "o" - obj {
                        "o" - obj {
                            "o" - 666
                            "k" - arr[
                                1,
                                888,
                                "bbb",
                                obj {
                                    "222" - 1
                                }
                            ]
                        }
                    }
                }
                "o" - 888
                "oo" - obj {
                    "ooo" - null
                }
                "222" - false
            },
            false
        ]
        "\"raw\":\"aaaa\"".raw()
        "d" - null
        "f" - another
    }.prl()
}

fun Any.prl() = println(this)