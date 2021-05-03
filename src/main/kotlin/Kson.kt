open class Kson(var isPretty: Boolean = false, var level: Int = 0) {
    val sb by lazy { StringBuilder() }

    fun wrapAppend(string: String): StringBuilder = sb.append('\"').append(string).append('\"')
    inline fun wrapLine(action: () -> Unit = {}) {
        if (!isPretty) {
            action()
            sb.append(',')
            return
        }
        repeat(level + 1) {
            sb.append('\t')
        }
        action()
        sb.append(',').append('\n')
    }

    operator fun String.minus(value: Nothing?) = wrapLine {
        wrapAppend(this).append(':').append("null")
    }

    inline operator fun <reified T> String.minus(value: T) = wrapLine {
        wrapAppend(this).append(':')
        if (value == null) sb.append("null")
        else when (value) {
            is Kson -> sb.append(value.apply { isPretty = this@Kson.isPretty;level = this@Kson.level + 1 }.toString())
            is Number, is Boolean -> sb.append(value.toString())
            else -> wrapAppend(value.toString())
        }
    }

    operator fun String.minus(kson: Kson) = wrapLine {
        wrapAppend(this).append(':').append(kson.toString())
    }

    fun Any.raw() = wrapLine {
        sb.append(this)
    }

    fun obj(action: Kson.() -> Unit) = Kson(isPretty, level + 1).apply(action)
    fun nbj(action: Kson.() -> Unit) = Kson(isPretty, level + 2).apply(action)
    val arr by lazy { KsonArray(isPretty, level + 1) }

    open fun wrapper(action: () -> Unit): String {
        sb.insert(0, '{')
        action()
        return sb.append('}').toString()
    }

    override fun toString(): String = wrapper {
        if (isPretty) {
            sb.insert(1, '\n')
            sb.deleteCharAt(sb.lastIndex).deleteCharAt(sb.lastIndex).append('\n')
            repeat(level) { sb.append('\t') }
        } else sb.deleteCharAt(sb.lastIndex)
    }
}

fun kson(isPretty: Boolean = false, level: Int = 0, action: Kson.() -> Unit) = Kson(isPretty, level).apply(action)

class KsonArray(isPretty: Boolean = false, level: Int = 0) : Kson(isPretty, level) {
    inline operator fun <reified T> get(collection: Collection<T>) = get(collection.toTypedArray())
    inline operator fun <reified T> get(vararg values: T) = apply {
        sb.clear()
        values.forEach {
            wrapLine {
                if (it == null) {
                    sb.append("null")
                    return@wrapLine
                }
                when (it) {
                    is Kson -> sb.append(it.apply { isPretty = this@KsonArray.isPretty;level = this@KsonArray.level + 1 })
                    is Number, is Boolean -> sb.append(it)
                    else -> wrapAppend(it.toString())
                }
            }
        }
    }

    override fun wrapper(action: () -> Unit): String {
        sb.insert(0, '[')
        action()
        return sb.append(']').toString()
    }
}