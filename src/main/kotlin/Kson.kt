import java.util.*

open class Kson(var isPretty: Boolean = false, var level: Int = 0) {
    val list = LinkedList<String>()
    val sb by lazy { StringBuilder() }

    fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')

    operator fun String.minus(value: Nothing?) {
        sb.clear()
        wrapAppend(this).append(":null")
        list.add(sb.toString())
    }

    inline operator fun <reified T> String.minus(value: T) {
        sb.clear()
        wrapAppend(this).append(':')
        when (value) {
            is Kson -> sb.append(value.apply { isPretty = this@Kson.isPretty;level = this@Kson.level + 1 })
            is Number, is Boolean -> sb.append(value)
            else -> wrapAppend(value.toString())
        }
        list.add(sb.toString())
    }

    fun Any.raw() {
        list.add(this.toString())
    }

    val arr
        get() = KsonArray(isPretty, level + 1)

    fun obj(action: Kson.() -> Unit) = Kson(isPretty, level + 2).apply(action)

    open fun wrapper(action: () -> Unit): String {
        sb.clear().append('{')
        action()
        return sb.append('}').toString()
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

    override fun wrapper(action: () -> Unit): String {
        sb.clear().insert(0, '[')
        for (k in ksonList.indices)
            list[ksonListIndex[k]] = ksonList[k].apply { isPretty = this@KsonArray.isPretty;level = this@KsonArray.level + 1 }.toString()
        action()
        return sb.append(']').toString()
    }
}