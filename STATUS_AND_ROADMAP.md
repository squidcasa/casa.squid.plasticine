# STATUS AND ROADMAP.md

## Project Overview

**Plasticine** is an experimental GUI toolkit for Quil, providing UI
abstractions like components, containers, and layouts for Clojure graphics
applications. The library is intentionally minimal and Clojure-y, built around
the concept of atoms with metadata as component objects.

## Current Status

### Core Architecture (COMPLETE)

The fundamental architecture is solid and implemented:

- **Object System**: Components are atoms with methods stored in metadata (duck-typed, no inheritance)
- **Dispatch System**: Dynamic method dispatch via `casa.squid.plasticine.object`
- **Data Binding**: Two-way data binding between atoms with `bind<>` and `bind>`
- **Component Lifecycle**: Basic lifecycle with cleanup, drawing, and event handling
- **Mouse/Keyboard Events**: Full event dispatch system working

### Components (PARTIAL)

**Complete:**
- `hslider` - Horizontal slider with data binding
- `text` - Basic text rendering component
- `select-list` - List with keyboard navigation (up/down arrows)

**Containers:**
- `rows` - Vertical layout
- `cols` - Horizontal layout  
- `stack` - Stacked layout with gap support

**Incomplete:**
- `grid` - Started but has empty implementations (`grid-layout`, `grid-size`)

### Drawing System (COMPLETE)

- **Style Management**: `with-props` macro for temporary style changes
- **State Stack**: Proper Quil state management with push/pop semantics
- **Basic Shapes**: `border-rect`, `polygon` utilities
- **Text Rendering**: Basic text with alignment and sizing

### Event System (COMPLETE)

- **Mouse Events**: Full capture (pressed, released, clicked, moved, dragged, wheel)
- **Keyboard Events**: Key pressed/released/typed with focus management
- **Event Bubbling**: Mouse events properly dispatched to child components
- **Focus System**: Keyboard events route to focused component

## Issues and Idiosyncrasies

### 🔴 Critical Issues

3. **Grid Component Broken**: `grid.clj` has empty implementations that will crash

### 🟡 Design Quirks

1. **Component Identity**: Using atoms as component identity makes equality checks tricky
2. **Event Handler Registration**: Manual middleware setup required for each sketch
3. **Style Inheritance**: No cascading styles - each component must specify all props
4. **Layout Algorithm**: Simple but inflexible - no min/max size constraints in layouts
5. **State Management**: Global state (`*stack*`) in draw namespace could cause issues

### 🟢 Minor Issues

1. **Code Duplication**: `text-min-size` and `text-pref-size` have similar calculations
2. **Hardcoded Values**: Text size defaults scattered across files
3. **Missing Documentation**: Most functions lack docstrings
4. **Error Handling**: Minimal validation of component props

## Architecture Analysis

### Strengths

- **Minimal Core**: Only ~550 lines of code for full UI system
- **Clojure-y Design**: Leverages atoms, metadata, and functional patterns
- **Flexible**: Duck typing allows easy extension and modification
- **Data Binding**: Two-way binding eliminates manual state synchronization
- **No Dependencies**: Only depends on Quil

### Weaknesses

- **Performance**: No clipping/visibility optimization - everything redraws
- **Layout System**: Very basic, no flexbox-like capabilities
- **Styling**: No CSS-like system, all styles inline
- **Accessibility**: No ARIA or keyboard navigation beyond basic
- **Testing**: No test suite

## Most Sensible Next Steps

### Priority 1: Fix Critical Issues

1. **Fix Grid Component** - Complete `grid-layout` and `grid-size` implementations

### Priority 2: Essential Components

1. **Vertical Slider** (`vslider`) - Natural counterpart to `hslider`
2. **Button** - Basic clickable button with states (normal/hover/pressed)
3. **Checkbox** - Boolean toggle with label
4. **Text Input** - Single-line text field with cursor and selection

### Priority 3: Layout Improvements

1. **Flexible Layouts** - Add weight-based sizing like CSS flexbox
2. **Scrollable Containers** - Add scrollbars and viewport clipping
3. **Margin/Padding System** - More systematic spacing controls
4. **Alignment Options** - Center, right, bottom alignment in containers

### Priority 4: Developer Experience

1. **REPL-friendly** - Better development workflow
2. **Hot Reloading** - Component updates without restart
3. **Inspector** - Debug tool to inspect component tree
4. **Examples** - More comprehensive usage examples

### Priority 5: Performance

1. **Dirty Rectangles** - Only redraw changed regions
2. **Component Caching** - Cache rendered components
3. **Virtual Scrolling** - For large lists
4. **Debounced Rendering** - Prevent excessive redraws

## Usage Patterns to Document

### Good Patterns

```clojure
;; Using atoms as models
(def model (atom 50))
(def slider (p/hslider {:model model :min 0 :max 100}))

;; Composing layouts
(def ui (p/stack [slider (p/text "Value:" {:text-size 16})] :gap 10))
```

### Bad Patterns

```clojure
;; Don't create atoms in render path
(defn bad-component []
  (p/text (str (rand-int 100)))) ; New atom each render!

;; Don't forget cleanup for data binding
(defn bad-cleanup []
  (let [slider (p/hslider {:model (atom 50)})]
    ;; Missing cleanup will leak memory
    slider))
```

## Long-term Vision

The library is well-positioned to become a lightweight alternative to more complex UI frameworks. With the current foundation, it could evolve into:

1. **Game UI Toolkit** - Perfect for game interfaces and creative coding
2. **Data Visualization** - Interactive charts and controls
3. **Creative Coding** - Procedural art with interactive parameters
4. **Educational Tool** - Teaching Clojure + graphics programming

The experimental nature means breaking changes are expected, but the core atom+metadata pattern is solid and extensible.
