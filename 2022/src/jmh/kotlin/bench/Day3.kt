package bench

import Day3Fast
import Day3Func
import Day3Imp
import Day3Input
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

typealias D3Func = Day3Input
typealias D3Imp = Day3Input
typealias D3Fast = List<String>

@Suppress("unused")
@State(Scope.Benchmark)
open class Day3 : Bench3<D3Func, D3Imp, D3Fast>("day3", Day3Func, Day3Imp, Day3Fast)
