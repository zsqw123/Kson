open class NormalKson {
    private val sb by lazy { StringBuilder() }
    private fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')
    operator fun String.minus(value: NormalKson) {
        wrapAppend(this).append(':').append(value).append(',')
    }

    operator fun String.minus(value: NormalKsonArr) {
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

class PrettyKson(var level: Int = 0) {
    private val list = ArrayList<String?>()
    private val ksonList = ArrayList<PrettyKson>()
    private val arrList = ArrayList<PrettyKsonArr>()
    private val ksonListIndex = ArrayList<Int>()
    private val arrListIndex = ArrayList<Int>()
    private fun wrapAppend(string: String): StringBuilder = StringBuilder().append('\"').append(string).append('\"')

    operator fun String.minus(value: PrettyKson) {
        list.add(wrapAppend(this).append(':').toString())
        ksonList.add(value)
        ksonListIndex.add(list.lastIndex)
    }

    operator fun String.minus(value: PrettyKsonArr) {
        list.add(wrapAppend(this).append(':').toString())
        arrList.add(value)
        arrListIndex.add(list.lastIndex)
    }

    operator fun String.minus(value: Number) {
        list.add(wrapAppend(this).append(':').append(value).toString())
    }

    operator fun String.minus(value: Boolean) {
        list.add(wrapAppend(this).append(':').append(value).toString())
    }

    operator fun String.minus(value: Any?) {
        val sb = wrapAppend(this)
        if (value == null) sb.append(":null")
        else sb.append(":\"").append(value).append('\"')
        list.add(sb.toString())
    }

    fun Any.raw() = list.add(toString())

    val arr
        get() = PrettyKsonArr()

    inline fun obj(crossinline action: PrettyKson.() -> Unit) = PrettyKson(level + 1).apply(action)

    override fun toString(): String {
        for (k in ksonList.indices)
            list[ksonListIndex[k]] += ksonList[k].apply { level = this@PrettyKson.level + 1 }.toString()
        for (k in arrList.indices)
            list[arrListIndex[k]] += arrList[k].apply { level = this@PrettyKson.level + 1 }.toString()
        val whiteSpace = StringBuilder()
        repeat(level) { whiteSpace.append('\t') }
        val sp = StringBuilder(",\n").append(whiteSpace).append('\t')
        return StringBuilder().append('{').append('\n')
            .append(whiteSpace).append('\t').append(list.joinToString(sp)).append('\n')
            .append(whiteSpace).append('}').toString()
    }
}

val arr
    get() = NormalKsonArr()
val parr
    get() = PrettyKsonArr()

inline fun obj(crossinline action: NormalKson.() -> Unit) = NormalKson().apply(action)
inline fun pobj(crossinline action: PrettyKson.() -> Unit) = PrettyKson().apply(action)

class PrettyKsonArr(var level: Int = 0) {
    private val list = ArrayList<String?>()
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