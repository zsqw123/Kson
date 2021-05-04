fun main() {
    val another = parr[
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
    pobj {
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
    obj {}.prl() // empty object
    obj { // object contains key-value
        "awa" - 1
        "QwQ" - false
        "\"raw\":\"aaaa\"".raw()
    }.prl()
    arr.prl() // empty array
    arr[1, "s", null].prl() // array with elements
    arr[listOf("awa", null, 1, false)].prl()

    parr[
        pobj {
            "awa" - 1
            "QwQ" - false
            "qwq" - arr[listOf(1, null, false, "555")]
        }
    ].prl()
}

fun Any.prl() = println(this)