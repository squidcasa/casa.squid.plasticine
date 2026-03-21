In container.clj, there's now a `case` statement on a function value, because
the two containers use the same min-size and pref-size function. Instead, split
these functions up
