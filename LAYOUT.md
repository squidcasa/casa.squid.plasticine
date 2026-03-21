# Constraint-Based Layout System

The plasticine component system uses a constraint-based layout approach that allows components to specify how they should be sized within given constraints. This system replaces the legacy min/max/preferred sizing with a more flexible and powerful approach.

## Core Concepts

### Constraint-Based Layout

Instead of fixed sizes, components receive `[min-width max-width min-height max-height]` constraints and return `[width height]` that fits within these constraints. This allows for responsive layouts that adapt to available space.

### Fixed vs Flex Sizing

Components can have:
- **Fixed sizing**: Use intrinsic dimensions based on content
- **Flex sizing**: Use `flex-width` and/or `flex-height` attributes to indicate how they should grow when extra space is available

### Intrinsic Sizing

Components provide intrinsic sizing methods to indicate their natural dimensions:
- `-min-intrinsic-width`: Absolute minimum width needed
- `-max-intrinsic-width`: Ideal width for the component
- `-min-intrinsic-height`: Absolute minimum height needed  
- `-max-intrinsic-height`: Ideal height for the component

## Component Interface

### Required Methods

All components must implement:

```clojure
;; Main layout method
- -layout-size [this constraints] → [width height]
;; Calculate size for this component given constraints

;; Intrinsic sizing (optional, provide defaults)
- -min-intrinsic-width [this height] → min-width
- -max-intrinsic-width [this height] → max-width  
- -min-intrinsic-height [this width] → min-height
- -max-intrinsic-height [this width] → max-height
```

### Helper Functions

```clojure
;; Access flex attributes
(c/flex-width component) → flex-width value
(c/flex-height component) → flex-height value

;; Layout sizing
(c/layout-size component constraints) → [width height]
```

## Container Layout

### Cols Container

Arranges children horizontally with optional gaps and flex distribution:

```clojure
(container/cols [child1 child2 child3] :gap 10)
```

**Layout Algorithm:**
1. Calculate intrinsic sizes for all children
2. Sum fixed widths and add gaps
3. Distribute remaining space to flex children based on flex ratios
4. Height is the maximum of all child heights

### Stack Container

Arranges children vertically with optional gaps and flex distribution:

```clojure
(container/stack [child1 child2 child3] :gap 5)
```

**Layout Algorithm:**
1. Calculate intrinsic sizes for all children  
2. Sum fixed heights and add gaps
3. Distribute remaining space to flex children based on flex ratios
4. Width is the maximum of all child widths

### Grid Container

Arranges children in a grid with configurable rows/columns:

```clojure
(grid/grid :cols 3 :children [child1 child2 child3 ...])
```

**Auto-sizing:** When no explicit rows/cols provided, calculates optimal layout based on number of children.

## Examples

### 1. Simple Fixed Component

```clojure
(defn simple-component [_]
  {:width 100 :height 50})

;; Component implementation
(def simple-meta
  {:-layout-size (fn [_ _] [100 50])})
```

### 2. Text Component with Intrinsic Sizing

```clojure
(defn text-layout-size [{:keys [text]} [min-w max-w min-h max-h]]
  (let [text-size (d/prop :text-size 12)
        text-width (q/text-width text)
        preferred-width (+ text-width text-size)
        preferred-height (* 2 text-size)]
    [(min (max preferred-width min-w) max-w)
     (min (max preferred-height min-h) max-h)]))
```

### 3. Component with Flex Attributes

```clojure
(defn flex-component [props]
  (atom (merge {:width 50 :height 30} props)
         :meta {:-layout-size (fn [this constraints]
                                (let [[min-w max-w min-h max-h] constraints
                                      flex-w (:flex-width this 0)
                                      flex-h (:flex-height this 0)
                                      base-w (:width this 50)
                                      base-h (:height this 30)]
                                  ;; For flex components, use flex-based sizing
                                  [(cond
                                     (> flex-w 0) (max base-w (min max-w (+ base-w (* flex-w (- max-w base-w)))))
                                     :else (min (max base-w min-w) max-w))
                                   (cond
                                     (> flex-h 0) (max base-h (min max-h (+ base-h (* flex-h (- max-h base-h)))))
                                     :else (min (max base-h min-h) max-h))]))}))
```

### 4. Complex Layout Example

```clojure
;; Create a responsive layout
(let [header (text/text "Header" {:flex-height 1})
      content (text/text "Content goes here" {:flex-width 3 :flex-height 4})
      sidebar (text/text "Sidebar" {:flex-width 1 :flex-height 4})
      footer (text/text "Footer" {:flex-height 1})]
  
  (container/cols
    [sidebar
     (container/stack [header content footer] :gap 5)]
    :gap 10))
```

## Practical Tips

### For Component Authors

1. **Always provide sensible defaults**: Components should have reasonable intrinsic sizes
2. **Handle edge cases**: Account for zero/negative constraints
3. **Use util/clamped+**: Prevent integer overflow when adding sizes
4. **Test with various constraints**: Ensure components behave well under tight constraints

### For Layout Designers

1. **Start with fixed sizing**: Begin with components that have clear intrinsic sizes
2. **Add flex gradually**: Introduce flex attributes only when needed for responsiveness
3. **Test constraint scenarios**: Verify layouts work with different screen sizes
4. **Use gaps thoughtfully**: Account for gaps in total layout calculations

### Common Patterns

#### Responsive Text
```clojure
(defn responsive-text [text]
  (text/text text {:flex-width 1 :max-width 400}))
```

#### Centered Component
```clojure
(defn centered [child]
  (container/cols [child] :gap 0))
```

#### Proportional Layout
```clojure
(defn three-column [left center right]
  (container/cols [left center right] :gap 10))
```

## Migration from Legacy System

### Old → New
- Remove `-min-size`, `-max-size`, `-pref-size` methods
- Implement `-layout-size` method with constraint handling
- Use flex attributes instead of preferred sizing hints
- Leverage intrinsic sizing methods for natural component behavior

### Performance Considerations
- Layout calculations are cached when constraints don't change
- Avoid expensive calculations in intrinsic sizing methods
- Use fixed sizes for components that don't need flexibility