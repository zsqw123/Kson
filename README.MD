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

// we can combine these elements whatever you want
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

Just need pass in the `true`, all child object will be pretty.

```kotlin
obj(true) { //... }
arr(true) [ //... ]
//TODO Notice: There are some problems when use arr 
//    as parent and parameter is true!!
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

