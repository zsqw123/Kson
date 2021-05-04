import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.Options

import org.openjdk.jmh.runner.options.OptionsBuilder
import t.Ben

fun timer(method: () -> Unit): Long {
    val s = System.nanoTime()
    method()
    return System.nanoTime() - s
}

fun main() {
//    val ben = Ben()
    val opt: Options = OptionsBuilder()
        .include(Ben::class.java.simpleName) // 指明本次要跑的类
        .forks(1) // fork JVM的数量
        .build()

    Runner(opt).run()
//    timer { ben.jsonWithBigArray().prl() }.prl()
//    timer { ben.ksonWithBigArray().prl() }.prl()
//    timer { ben.jsonWithBigArrayPretty() }.prl()
//    timer { ben.ksonWithBigArrayPretty() }.prl()
}