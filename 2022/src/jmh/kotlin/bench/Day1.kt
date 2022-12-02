package bench

import Day1Fast
import Day1Func
import Day1Imp
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

typealias D1Func = List<List<Int>>
typealias D1Imp = D1Func
typealias D1Fast = List<IntArray>

@Suppress("unused")
@State(Scope.Benchmark)
open class Day1 : Bench3<D1Func, D1Imp, D1Fast>("day1", Day1Func, Day1Imp, Day1Fast)
