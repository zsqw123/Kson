## Kson

[`简体中文 zh-CN`](./READMECN.MD)

A lightweight `Kotlin` DSL to render `Json` to `String`.

Code: [Kson.kt](https://github.com/zsqw123/Kson/blob/master/src/main/kotlin/Kson.kt)

### DSL

```kotlin

obj{} // empty object
obj { // object contains key-value
    "awa" - 1
    "QwQ" - false
    "\"raw\":\"aaaa\"".raw() // raw json is allowed
}
arr // empty array
arr[1, "s", null] // array with elements
arr[listOf("awa",null,1,false)] // it can provided by any collections

// you can combine these elements whatever you want
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

When pass in the `pobj`, All children object `obj`, `arr` will be pretty.

When pass in the `parr`, Only those who are directly related elements will be pretty.

(*beacuse `parr` not change the namespace*)

```kotlin
pobj {
    obj{
        "1" - arr[
            arr[
                obj{
                    // work well, it will pretty print all children
                }
            ]
        ]
    }
}

parr [
    1,
    "awa", // directly related child will be render
    arr[ // this arr will not pretty render
        pobj{
            // `pobj` change the namespace, so here can be pretty render
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

### Benchmark

Benchmarks have been conducted with the [jmh](https://openjdk.java.net/projects/code-tools/jmh/) OpenJDK tool. Benchmark can be found in [BenchMark.kt](https://github.com/zsqw123/Kson/blob/master/src/test/kotlin/BenchMark.kt)

`Kson` was put side to side with one of the most popular JSON builder for `Java` : [JSON-java](https://github.com/stleary/JSON-java)

Testing environment : *AMD Ryzen 7 4800U, 8 cores 16 threads, VM version: JDK 1.8.0_282, OpenJDK 64-Bit Server VM, 25.282-b08*

Score in operations/second (throughput mode), higher is better

| Benchmark                 | Score     | Error     | Unit  |
| ------------------------- | --------- | --------- | ----- |
| jsonWithBigArray          | 15746.152 | ± 524.543 | ops/s |
| ksonWithBigArray          | 21958.445 | ± 144.323 | ops/s |
| jsonWithBigArray (Pretty) | 7477.957  | ± 115.563 | ops/s |
| ksonWithBigArray (Pretty) | 8568.294  | ± 43.227  | ops/s |

#### Thanks

1. This repository inspired by this repo [lectra-tech/koson](https://github.com/lectra-tech/koson), Only reference its api, But the implementation is not same.
2. Why use `-` ?? Look this:

![](https://cdn.jsdelivr.net/gh/zsqw123/cdn@master/picCDN/20210504144051.png)