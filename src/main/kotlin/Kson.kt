import java.util.*
import kotlin.collections.ArrayList

open class NormalKson {
    private val sb by lazy { StringBuilder() }
    private fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')
    operator fun String.minus(value: NormalKson) {
        wrapAppend(this).append(':').append(value).append(',')
    }

    fun String.minus(value: NormalKsonArr) {
        wrapAppend(this).append(':').append(value).append(',')
    }

    operator fun String.minus(value: Number) {
        wrapAppend(this).append(':').append(value).append(',')
    }

    operator fun String.minus(value: Boolean) {
        wrapAppend(this).append(':').append(value).append(',')
    }

    operator fun String.minus(value: Any?) {
        if (value == null) wrapAppend(this).append(":null")
        else {
            wrapAppend(this).append(':')
            wrapAppend(value.toString())
        }
        sb.append(',')
    }

    fun Any.raw() {
        sb.append(this).append(',')
    }

    override fun toString(): String {
        if (sb.isEmpty()) sb.append('.')
        return sb.insert(0, '{').deleteCharAt(sb.lastIndex).append('}').toString()
    }
}

class NormalKsonArr {
    private val sb = StringBuilder()
    private fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')
    operator fun get(collection: Collection<Any?>) = get(*collection.toTypedArray())
    operator fun get(vararg array: Any?) = apply {
        sb.clear()
        array.forEach {
            if (it == null) sb.append("null")
            else when (it) {
                is Number, is Boolean, is NormalKson, is NormalKsonArr -> sb.append(it)
                else -> wrapAppend(it.toString())
            }
            sb.append(',')
        }
    }

    override fun toString(): String {
        if (sb.isEmpty()) sb.append('.')
        return sb.insert(0, '[').deleteCharAt(sb.lastIndex).append(']').toString()
    }
}

open class PrettyKson(l: Int = 0) {
    var level: Int = l
    private val list = LinkedList<String>()
    private val sb by lazy { StringBuilder() }
    private fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')
    private inline fun wrapAdd(method: () -> Unit) {
        sb.clear()
        method()
        list.add(sb.toString())
    }

    operator fun String.minus(value: PrettyKson) = wrapAdd {
        wrapAppend(this).append(':').append(value.apply { level = this@PrettyKson.level + 1 })
    }

    fun String.minus(value: PrettyKsonArr) = wrapAdd {
        wrapAppend(this).append(':').append(value.apply { level = this@PrettyKson.level + 1 })
    }

    operator fun String.minus(value: Number) = wrapAdd {
        wrapAppend(this).append(':').append(value)
    }

    operator fun String.minus(value: Boolean) = wrapAdd {
        wrapAppend(this).append(':').append(value)
    }

    operator fun String.minus(value: Any?) = wrapAdd {
        if (value == null) wrapAppend(this).append(":null")
        else {
            wrapAppend(this).append(':')
            wrapAppend(value.toString())
        }
    }

    fun Any.raw() {
        list.add(toString())
    }

    val arr
        get() = PrettyKsonArr(level + 1)

    inline fun obj(crossinline action: PrettyKson.() -> Unit) = PrettyKson(level + 2).apply(action)

    override fun toString(): String {
        sb.clear()
        sb.append('{')
        val whiteSpace = StringBuilder()
        repeat(level) { whiteSpace.append('\t') }
        val sp = StringBuilder(",\n").append(whiteSpace).append('\t')
        sb.append('\n').append(whiteSpace).append('\t').append(list.joinToString(sp))
        return sb.append('\n').append(whiteSpace).append('}').toString()
    }
}

val parr
    get() = PrettyKsonArr(0)
val arr
    get() = NormalKsonArr()

inline fun obj(crossinline action: NormalKson.() -> Unit) = NormalKson().apply(action)
inline fun pobj(action: PrettyKson.() -> Unit) = PrettyKson().apply(action)

class PrettyKsonArr(var level: Int) {
    private val list = LinkedList<String?>()
    private val ksonList = ArrayList<PrettyKson>()
    private val arrList = ArrayList<PrettyKsonArr>()
    private val ksonListIndex = ArrayList<Int>()
    private val arrListIndex = ArrayList<Int>()
    private fun wrapAppend(string: String): StringBuilder = StringBuilder().append('\"').append(string).append('\"')
    operator fun get(collection: Collection<Any?>) = apply {
        collection.forEach {
            if (it == null) list.add("null")
            else when (it) {
                is PrettyKson -> {
                    list.add(null)
                    ksonList.add(it)
                    ksonListIndex.add(list.lastIndex)
                }
                is Number, is Boolean -> list.add(it.toString())
                else -> list.add(wrapAppend(it.toString()).toString())
            }
        }
    }

    operator fun get(vararg array: Any?) = apply {
        array.forEach {
            if (it == null) list.add("null")
            else when (it) {
                is PrettyKson -> {
                    list.add(null)
                    ksonList.add(it)
                    ksonListIndex.add(list.lastIndex)
                }
                is PrettyKsonArr -> {
                    list.add(null)
                    arrList.add(it)
                    arrListIndex.add(list.lastIndex)
                }
                is Number, is Boolean -> list.add(it.toString())
                else -> list.add(wrapAppend(it.toString()).toString())
            }
        }
    }

    override fun toString(): String {
        for (k in ksonList.indices)
            list[ksonListIndex[k]] = ksonList[k].apply { level = this@PrettyKsonArr.level + 1 }.toString()
        for (k in arrList.indices)
            list[arrListIndex[k]] = arrList[k].apply { level = this@PrettyKsonArr.level + 1 }.toString()
        val whiteSpace = StringBuilder()
        repeat(level) { whiteSpace.append('\t') }
        val sp = StringBuilder(",\n").append(whiteSpace).append('\t')
        return StringBuilder().append('[').append('\n')
            .append(whiteSpace).append('\t').append(list.joinToString(sp)).append('\n')
            .append(whiteSpace).append(']').toString()
    }
}