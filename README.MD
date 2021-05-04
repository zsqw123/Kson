## Kson

A lightweight `Kotlin` DSL to render `Json` to `String`.

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

when pass in the `pobj`, All child object `obj`, `arr` will be pretty.

when pass in the `parr`, Only those who are directly related `obj` will be pretty.

*beacuse `parr` not change the namespace*

```kotlin
pobj {
    obj{
        "1" - arr[
            arr[
                obj{
                    // work well, it well pretty print
                }
            ]
        ]
    }
}
parr [
    parr[
        
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

