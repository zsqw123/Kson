import java.util.*

open class Kson(var isPretty: Boolean = false, var level: Int = 0) {
    val list = LinkedList<String>()
    private val sb by lazy { StringBuilder() }
    open val pre = '{'
    open val end = '}'

    fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')
    private inline fun wrapAdd(method: () -> Unit) {
        sb.clear()
        method()
        list.add(sb.toString())
    }

    operator fun String.minus(value: Nothing?) = wrapAdd {
        wrapAppend(this).append(":null")
    }

    operator fun String.minus(value: Kson) = wrapAdd {
        wrapAppend(this).append(':')
        sb.append(value.apply { isPretty = this@Kson.isPretty;level = this@Kson.level + 1 })
    }

    operator fun String.minus(value: Number) = wrapAdd {
        wrapAppend(this).append(':')
        sb.append(value)
    }

    operator fun String.minus(value: Boolean) = wrapAdd {
        wrapAppend(this).append(':')
        sb.append(value)
    }

    operator fun String.minus(value: Any) = wrapAdd {
        wrapAppend(this).append(':')
        wrapAppend(value.toString())
    }

    fun Any.raw() {
        list.add(this.toString())
    }

    val arr
        get() = KsonArray(isPretty, level + 1)

    inline fun obj(action: Kson.() -> Unit) = Kson(isPretty, level + 2).apply(action)

    private inline fun wrapper(action: () -> Unit): String {
        sb.clear().append(pre)
        action()
        return sb.append(end).toString()
    }

    override fun toString(): String = wrapper {
        if (isPretty) {
            val pre = StringBuilder()
            repeat(level) { pre.append('\t') }
            val sp = StringBuilder(",\n").append(pre).append('\t').toString()
            sb.append('\n').append(pre).append('\t').append(list.joinToString(sp))
            sb.append('\n').append(pre)
        } else sb.append(list.joinToString(","))
    }
}

val arr
    get() = KsonArray()

fun obj(isPretty: Boolean = false, action: Kson.() -> Unit) = Kson(isPretty).apply(action)

class KsonArray(isPretty: Boolean = false, level: Int = 0) : Kson(isPretty, level) {
    val ksonList = ArrayList<Kson>()
    val ksonListIndex = ArrayList<Int>()
    override val pre = '['
    override val end = ']'

    operator fun get(collection: Collection<Any?>) = apply {
        collection.forEach {
            if (it == null) list.add("null")
            else when (it) {
                is Kson -> {
                    list.add("")
                    ksonList.add(it)
                    ksonListIndex.add(list.lastIndex)
                }
                is Number, is Boolean -> list.add(it.toString())
                else -> list.add(wrapAppend(it.toString()).toString())
            }
        }
    }

    inline operator fun <reified T> get(vararg v: T) = apply {
        v.forEach {
            if (it == null) list.add("null")
            else when (it) {
                is Kson -> {
                    list.add("")
                    ksonList.add(it)
                    ksonListIndex.add(list.lastIndex)
                }
                is Number, is Boolean -> list.add(it.toString())
                else -> list.add(wrapAppend(it.toString()).toString())
            }
        }
    }

    operator fun invoke(pretty: Boolean) = apply {
        isPretty = pretty
    }

    override fun toString(): String {
        for (k in ksonList.indices)
            list[ksonListIndex[k]] = ksonList[k].apply { isPretty = this@KsonArray.isPretty;level = this@KsonArray.level + 1 }.toString()
        return super.toString()
    }
}