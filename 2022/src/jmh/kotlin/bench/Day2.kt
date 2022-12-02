package bench

import Day2Fast
import Day2Func
import Day2Imp
import Play
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

typealias D2Func = List<Pair<Play, Char>>
typealias D2Imp = D2Func
typealias D2Fast = IntArray

@Suppress("unused")
@State(Scope.Benchmark)
open class Day2 : Bench3<D2Func, D2Imp, D2Fast>("day2", Day2Func, Day2Imp, Day2Fast)
