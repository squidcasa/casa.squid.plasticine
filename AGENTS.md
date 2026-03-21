Avoid unnecessary calls to `vec`, use Clojure sequence abstractions wherever
applicable.

Use `;;` (double semicolon) for most comments, only use `;` (single semicolon)
for end-of-line comments.

Add docstrings to functions and namespaces.

Add the following to the top of each new clojure file you create

```
;; WARNING: An LLM generated this namespace. Some things may not make sense,
;; contain numerous bugs, and generally be frustrating to work with. Tread
;; carefully.
```

If the file already exists and does not have such a warning, instead add a
header like this, or if there is already such a header, add to it.

```
;; WARNING: This file was written by a human, but has since been expanded by an LLM. Tread carefully.
;; LLM HISTORY:
;; - <date> <model>: <summary of changes>
;; - <date> <model>: <summary of changes>
;; - <date> <model>: <summary of changes>
```

E.g.
```
;; WARNING: This file was written by a human, but has since been expanded by an LLM. Tread carefully.
;; LLM HISTORY:
;; - 2026-02-08 SomeGPT 3.0: Fixed layout to account for min/max/pref sizing
```

Be careful not to shadow core functions like `max`, `min`, `count` etc. When destructuring, you can rebind to a non-conflicting name, e.g `{max-val :max min-val :min}`

Don't use `>` or `>=`, always use `<` or `<=`, flipping the arguments as necessary.

When adding values where one of the values might be `Long/MAX_VALUE`, use `util/clamped+` to prevent overflow. Useful when computing layout constraints.

Be extra careful with having matching amounts of opening and closing parentheses, especially at the end of functions, or at the end of a let binding vector.
