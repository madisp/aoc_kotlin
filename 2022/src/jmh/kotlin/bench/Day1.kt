package bench

import Day1Fast
import Day1Func
import Day1Imp
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

typealias Func = List<List<Int>>
typealias Imp = Func
typealias Fast = List<IntArray>

@Suppress("unused")
@State(Scope.Benchmark)
open class Day1 : DayBench<Func, Imp, Fast>("day1", Day1Func, Day1Imp, Day1Fast)
