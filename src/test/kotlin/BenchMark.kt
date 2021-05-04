fun timer(method: () -> Unit): Long {
    val s = System.nanoTime()
    method()
    return System.nanoTime() - s
}

fun main() {
    timer { jsonWithBigArray() }.prl()
    timer { ksonWithBigArray().prl() }.prl()
}