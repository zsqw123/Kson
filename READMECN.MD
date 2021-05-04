## Kson

轻量级的使用 `Kotlin DSL` 将 `Json` 渲染为 `String` 的渲染器

### DSL 用法

```kotlin

obj{} // empty object
obj { // object contains key-value
    "awa" - 1
    "QwQ" - false
    "\"raw\":\"aaaa\"".raw() // raw json is allowed
}
arr // empty array
arr[1, "s", null] // 可以直接塞元素
arr[listOf("awa",null,1,false)] // 也可也塞个集合

// 随便怎么瞎组都可以
arr[
    1, 
    2,
    obj {
        "awa" - 1
        "QwQ" - false
        "qwq" - arr[
            3,
            4
        ]
    }
]
```

#### ouputs

```json
{}
{"awa":1,"QwQ":false,"raw":"aaaa"}
[]
[1,"s",null]
["awa",null,1,false]
[1,2,{"awa":1,"QwQ":false,"qwq":[3,4]}]
```

#### Pretty outputs

可以使用 `pobj`, 所有的 `子obj`, `子arr`, `子元素` 都可以被渲染

如果使用 `parr`, 只有直接相关的`子元素`才会被渲染

(*因为 `parr` 并没有改变命名空间*)

```kotlin
pobj {
    obj{
        "1" - arr[
            arr[
                obj{
                    // 这样好 子元素可以被渲染
                }
            ]
        ]
    }
}

parr [
    1,
    "awa", // 直接相关的子元素可以被渲染
    arr[ // 这个就不会有 Pretty 渲染了, 会直接普通渲染
        pobj{
            // `pobj` 改变了命名空间, 下面的就可以优化渲染了
        }
    ]
]
```

output like this:

```json
{
	"awa":1,
	"QwQ":false,
	"qwq":[
		3,
		4
	]
}
```

### 跑个分

跑分使用了 OpenJDK [jmh](https://openjdk.java.net/projects/code-tools/jmh/). 可以在 [BenchMark.kt](https://github.com/zsqw123/Kson/blob/master/src/test/kotlin/BenchMark.kt) 查看具体测试

测试对象是 `Kson` 与最流行的JSON构建器之一 : [`JSON-java`](https://github.com/stleary/JSON-java)

测试环境: *AMD Ryzen 7 4800U, 8 cores 16 threads, VM version: JDK 1.8.0_282, OpenJDK 64-Bit Server VM, 25.282-b08*

每秒操作数得分（吞吐量模式），`Score` 越高越好, `Error` 绝对值越低越稳定

| Benchmark                 | Score     | Error     | Unit  |
| ------------------------- | --------- | --------- | ----- |
| jsonWithBigArray          | 15746.152 | ± 524.543 | ops/s |
| ksonWithBigArray          | 21958.445 | ± 144.323 | ops/s |
| jsonWithBigArray (Pretty) | 7477.957  | ± 115.563 | ops/s |
| ksonWithBigArray (Pretty) | 8532.050  | ± 43.227  | ops/s |

可以看到尽管 `Kotlin Lambda` 存在性能开销, 性能也要远优于 `JSON-java`

#### Thanks

1. 灵感来源于 [lectra-tech/koson](https://github.com/lectra-tech/koson), 仅借鉴其 `api`, 实现并不一致
2. 为什么要用 `-` ?? 见下图:

![](https://cdn.jsdelivr.net/gh/zsqw123/cdn@master/picCDN/20210504144051.png)