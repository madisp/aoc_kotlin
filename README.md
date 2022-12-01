# My Advent of Code solutions

See me write these live on [Twitch](https://twitch.tv/madisp) at UTC 05:00, just as the puzzle opens.
The VODs are typically available for a week or so.

Links to years with some solution explanations:

- [2022](2022)
- [2021](2021)
- [2015](2015)

## Layout

Years are each a separate Gradle submodule. For fun I solve most of the puzzles three times:

- a functional solution, the code must be 100% pure and immutable, no `var`, no `MutableList` etc.
  Some leeway here is helper functions in the `libs` folder, some of these may not be implemented
  completely functionally but in that case the API surface has to be functional and the data structures
  used must remain immutable.
- an imperative solution, mutable state is allowed and I use for loops and the likes freely.
- a fast solution, where I try to write as fast Kotlin code as possible while giving zero consideration
  to code readability. Starting from 2022 I started using JMH to benchmark these solutions.

If a solution has multiple variants I use `_func`, `_imp` and `_fast` suffixes for these.

## Utils library

During 2021 I wrote a bunch of small utilities and data structures to help me solve the puzzles.
A non-exhausting list:

- A generic [base class](lib/src/main/kotlin/utils/Solution.kt) for writing solutions - they take a `Parser`
  and have two overridable methods called `part1` and `part2`. Both of these are expected to return
  a subclass of `Number`.
- A [`Parser`](lib/src/main/kotlin/utils/Parser.kt) framework to massage the input data
- Common data structures for representing input, a [`Grid`](lib/src/main/kotlin/utils/Grid.kt)
- (with a [`MutableGrid`](lib/src/main/kotlin/utils/MutableGrid.kt) imperative variant)
  and a [`Graph`](lib/src/main/kotlin/utils/Graph.kt)
- [Vec2i](lib/src/main/kotlin/utils/Vec2i.kt) and [Vec4i](lib/src/main/kotlin/utils/Vec4i.kt) to help
  solving 2D and 3D puzzles, respectively
- A small [utils](lib/src/main/kotlin/utils/utils.kt) for common collection & string operations
