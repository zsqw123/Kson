open class NormalKson {
    private val sb by lazy { StringBuilder() }
    private fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')
    private fun String.normalAppend(v: Any): StringBuilder = wrapAppend(this).append(':').append(v).append(',')
    operator fun String.minus(value: NormalKson) = normalAppend(value)
    operator fun String.minus(value: NormalKsonArr) = normalAppend(value)
    operator fun String.minus(value: Number) = normalAppend(value)
    operator fun String.minus(value: Boolean) = normalAppend(value)
    fun Any.raw(): StringBuilder = sb.append(this).append(',')

    operator fun String.minus(value: Any?) {
        if (value == null) wrapAppend(this).append(":null")
        else {
            wrapAppend(this).append(':')
            wrapAppend(value.toString())
        }
        sb.append(',')
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

class PrettyKson(var level: Int = 0) {
    private val list = ArrayList<StringBuilder>()
    private val ksonList = ArrayList<PrettyKson>()
    private val arrList = ArrayList<PrettyKsonArr>()
    private val ksonListIndex = ArrayList<Int>()
    private val arrListIndex = ArrayList<Int>()
    operator fun String.minus(value: Number) = list.add(wrapAppend(this).append(':').append(value))
    operator fun String.minus(value: Boolean) = list.add(wrapAppend(this).append(':').append(value))
    private fun wrapAppend(string: String): StringBuilder = StringBuilder().append('\"').append(string).append('\"')

    operator fun String.minus(value: PrettyKson) {
        list.add(wrapAppend(this).append(':'))
        ksonList.add(value)
        ksonListIndex.add(list.lastIndex)
    }

    operator fun String.minus(value: PrettyKsonArr) {
        list.add(wrapAppend(this).append(':'))
        arrList.add(value)
        arrListIndex.add(list.lastIndex)
    }

    operator fun String.minus(value: Any?) {
        val sb = wrapAppend(this)
        if (value == null) sb.append(":null")
        else sb.append(":\"").append(value).append('\"')
        list.add(sb)
    }

    fun Any.raw() = list.add(StringBuilder(this.toString()))

    val arr
        get() = PrettyKsonArr()

    inline fun obj(crossinline action: PrettyKson.() -> Unit) = PrettyKson(level + 1).apply(action)

    override fun toString(): String {
        for (k in ksonList.indices)
            list[ksonListIndex[k]].append(ksonList[k].apply { level = this@PrettyKson.level + 1 }.toString())
        for (k in arrList.indices)
            list[arrListIndex[k]].append(arrList[k].apply { level = this@PrettyKson.level + 1 }.toString())
        return list.toTypedArray().wrap(level, '{', '}').toString()
    }
}

val arr
    get() = NormalKsonArr()
val parr
    get() = PrettyKsonArr()

inline fun obj(crossinline action: NormalKson.() -> Unit) = NormalKson().apply(action)
inline fun pobj(crossinline action: PrettyKson.() -> Unit) = PrettyKson().apply(action)

class PrettyKsonArr(var level: Int = 0) {
    private lateinit var list: Array<StringBuilder>
    private val ksonList = ArrayList<PrettyKson>()
    private val arrList = ArrayList<PrettyKsonArr>()
    private val ksonListIndex = ArrayList<Int>()
    private val arrListIndex = ArrayList<Int>()
    private fun StringBuilder.wrapAppend(string: String) = append('\"').append(string).append('\"')
    operator fun get(collection: Collection<Any?>) = apply {
        list = Array(collection.size) { StringBuilder() }
        var idx = 0
        collection.forEach {
            if (it == null) list[idx++].append("null")
            else when (it) {
                is PrettyKson -> {
                    ksonList.add(it)
                    ksonListIndex.add(idx++)
                }
                is PrettyKsonArr -> {
                    arrList.add(it)
                    arrListIndex.add(idx++)
                }
                is Number, is Boolean -> list[idx++].append(it.toString())
                else -> list[idx++].wrapAppend(it.toString())
            }
        }
    }

    operator fun get(vararg array: Any?) = apply {
        list = Array(array.size) { StringBuilder() }
        var idx = 0
        array.forEach {
            if (it == null) list[idx++].append("null")
            else when (it) {
                is PrettyKson -> {
                    ksonList.add(it)
                    ksonListIndex.add(idx++)
                }
                is PrettyKsonArr -> {
                    arrList.add(it)
                    arrListIndex.add(idx++)
                }
                is Number, is Boolean -> list[idx++] = StringBuilder(it.toString())
                else -> list[idx++].wrapAppend(it.toString())
            }
        }
    }

    override fun toString(): String {
        if (!this::list.isInitialized) return "[]"
        for (k in ksonList.indices)
            list[ksonListIndex[k]].append(ksonList[k].apply { level = this@PrettyKsonArr.level + 1 }.toString())
        for (k in arrList.indices)
            list[arrListIndex[k]].append(arrList[k].apply { level = this@PrettyKsonArr.level + 1 }.toString())
        return list.wrap(level, '[', ']').toString()
    }
}

fun Array<StringBuilder>.wrap(tabCount: Int, pre: Char = '{', end: Char = '}'): StringBuilder {
    if (isEmpty()) return StringBuilder().append(pre).append(end)
    val whiteSpace = StringBuilder()
    repeat(tabCount) { whiteSpace.append('\t') }
    val sp = StringBuilder(",\n").append(whiteSpace).append('\t')

    val sb = StringBuilder().append(pre).append('\n').append(whiteSpace).append('\t')
    for (i in 0 until size - 1) sb.append(get(i)).append(sp)
    return sb.append(get(size - 1)).append('\n').append(whiteSpace).append(end)
}