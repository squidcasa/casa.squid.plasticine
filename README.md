# plasticine

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/casa.squid/plasticine)](https://cljdoc.org/d/casa.squid/plasticine) [![Clojars Project](https://img.shields.io/clojars/v/casa.squid/plasticine.svg)](https://clojars.org/casa.squid/plasticine)
<!-- /badges -->

A flexible, Clojure-y GUI toolkit for Quil that provides UI abstractions for creative coding projects.

## Overview

Plasticine brings UI components like sliders, rotary knobs, and container layouts to Quil sketches. It's designed for creative coders who want to add interactive controls to their graphics while maintaining Quil's drawing and styling paradigm.

## Features

- **Component-based UI**: Build interfaces from composable components
- **Flexible layouts**: Stack, row, and grid layouts for arranging components
- **Data binding**: Two-way binding between components and atoms
- **Duck-typed objects**: Components are atoms with metadata - no rigid types
- **Quil integration**: Seamless middleware for use with `quil.core/defsketch`
- **Custom styling**: Use Quil's familiar drawing primitives for styling
- **Event handling**: Mouse and keyboard event support with flexible dispatching

This library is still experimental, and could still drastically change.

<!-- installation -->
## Installation

To use the latest release, add the following to your `deps.edn` ([Clojure CLI](https://clojure.org/guides/deps_and_cli))

```
casa.squid/plasticine {:mvn/version "0.0.0"}
```

or add the following to your `project.clj` ([Leiningen](https://leiningen.org/))

```
[casa.squid/plasticine "0.0.0"]
```
<!-- /installation -->

## Rationale

[Quil](http://quil.info/) is one of the most accessible ways to do graphics
programming to Clojure, but it's not so great when trying to do actual UI.
Things like buttons and sliders and text fields. There are, admittedly, better
tools to do that job, but sometimes you want to add a bit of UI to a Quil
sketch, or leverage Quil's drawing and styling paradigm to make your own very
custom UIs with just the right vibe you were going for.

So Plasticine is an attempt to provide some common UI abstractions, like
components, containers, and layouts. It's meant to be very flexible, and very
Clojure-y.

To have discernable components you need some kind of way to represent a mutable
object, however I wanted to avoid the rigidity of `deftype`/`defrecord`, and the
inevitable need for base types and inheritance that would pop up. So I tried to
come up with the most basic and most clojure-y representation of an object like
thing that I could think of that would provide the necessary flexibility.

Components in Plasticine are atoms with metadata. The object state (user
supplied data, i.e. the "model") is kept in the atom itself. The metadata on the
atom contains method implementations, and any other "secondary" data.

Things are 100% duck typed, there are no discernable types as such, just bags of
methods/functions (or you can think of them as prototypes if you wish).
"Classes" are maps of methods, mixins are maps of methods.

## Core Concepts

### The Object System

Plasticine uses a unique object system based on Clojure's atoms and metadata:

```clj
;; An object is just an atom with metadata
(def my-object
  (atom {:state "data"}
        :meta {:-draw (fn [this x y w h]
                        (println "Drawing" @this))
                :-mouse-pressed (fn [this event]
                                   (println "Clicked"))}))
```

**Key principles:**

1. **State in atom**: The atom's value holds the component's mutable state (the "model")
2. **Methods in metadata**: Method implementations are stored as functions in the atom's metadata
3. **Dynamic dispatch**: Methods are invoked using duck typing - no type checking required
4. **Mixins as maps**: You can compose behavior by merging maps of methods

**Dispatching methods:**

```clj
;; Using dispatch - returns nil if method not found
(dispatch my-object :-draw 0 0 100 100)

;; Using dispatch! - throws if method not found
(dispatch! my-object :-draw 0 0 100 100)

;; Defining named dispatch functions with defaults
(o/defdispatch draw [this x y w h]
  (println "Default draw implementation"))
```

**Creating custom objects:**

```clj
;; Define methods
(def my-methods
  {:-draw (fn [this x y w h]
            (q/rect x y w h))
   :-mouse-pressed (fn [this event]
                     (swap! this update :click-count inc))})

;; Create object with methods
(def my-widget
  (atom {:click-count 0}
        :meta my-methods))
```

This design provides:
- No rigid type hierarchies or inheritance
- Composition over inheritance
- Easy to understand and debug
- Full Clojure interop

**Mixins and method composition:**

```clj
;; Define reusable behavior as mixins
(def draggable-mixin
  {:-mouse-pressed (fn [this event]
                     (swap! this assoc :dragging true))
   :-mouse-dragged (fn [this event]
                     (when (:dragging @this)
                       (swap! this update :x + (:dx event))
                       (swap! this update :y + (:dy event))))
   :-mouse-released (fn [this event]
                      (swap! this assoc :dragging false))})

;; Mix behaviors together
(def my-draggable-widget
  (atom {:x 0 :y 0}
        :meta (merge draggable-mixin
                     {:-draw (fn [this x y w h]
                               (q/rect (:x @this) (:y @this) 50 50))})))
```

### Components

Components are built on the object system described above. A component is an atom with metadata that implements specific methods:

- `:-draw` - Renders the component
- `:-min-size` / `:-max-size` / `:-pref-size` - Size constraints
- `:-mouse-pressed` / `:-mouse-dragged` / etc. - Mouse event handlers
- `:-key-pressed` / `:-key-released` / `:-key-typed` - Keyboard event handlers
- `:-cleanup` - Resource cleanup when component is removed
- `:focusable?` - Whether component can receive keyboard focus
- `:props` - Default drawing properties for the component

The component metadata may also contain:
- Style properties
- Event handlers
- Layout parameters (margin, gap, etc.)

### Data Binding

Plasticine supports one-way and two-way data binding between components and atoms:

```clj
;; One-way binding: source -> destination
(bind> source-atom [:key] dest-atom [:dest-key])

;; Two-way binding
(bind<> source-atom [:key] dest-atom [:dest-key])

;; Many components support :model for automatic binding
(hslider {:model my-atom})
```

### The Root Component

The root component is the top-level container that manages:
- Dirty tracking for efficient redraws
- Focus management for keyboard events
- Layout boundaries

Mark the root as dirty to trigger a redraw: `(c/mark-dirty! component)`

## Components

### Creating Custom Components

You can create custom components by implementing the required methods:

```clj
(defn my-widget-draw [{:keys [color]} x y w h]
  (q/fill color)
  (q/rect x y w h))

(def my-widget-meta
  {:-draw          #'my-widget-draw
   :-pref-size     (fn [this] [100 100])
   :-mouse-pressed (fn [this event]
                     (swap! this update :clicks inc))})

(defn my-widget
  [{:keys [color] :as opts}]
  (atom (merge {:color [255 0 0] :clicks 0} opts)
        :meta my-widget-meta))
```

Use it like any other component:

```clj
(def app
  (stack [(text "Custom Widget")
          (my-widget {:color [100 200 255]})
          (text "Click to increment!")]))
```

### Sliders

Horizontal and vertical sliders for numeric values:

```clj
(hslider {:min 0
          :max 100
          :value 50
          :step 1
          :format #(str "Value: " %)
          :on-change #(println "New value:" %)
          :height 30})

(vslider {:min 0
          :max 100
          :value 50
          :width 30})
```

### Rotary Knobs

Rotary knobs controlled by dragging:

```clj
(rotary {:min 0
         :max 100
         :value 50
         :size 60
         :on-change #(println "Rotated to:" %)})
```

### Text

Display text with customizable styling:

```clj
(text "Hello, Plasticine!" {:text-size 24
                           :fill [255 100 100]
                           :text-align :center})
```

### Select List

Selectable list items with keyboard navigation (up/down arrows):

```clj
(select-list [(text "Option 1")
              (text "Option 2")
              (text "Option 3")])
```

## Layouts

### Stack Layout

Vertical stack of components:

```clj
(stack [(text "Title")
        (hslider {:value 50})
        (text "Description")]
       :margin 10
       :gap 5)
```

### Grid Layout

Flexible 2D grid with weighted columns and rows:

```clj
(grid :cols 3
      :rows 2
      :children [rot1 rot2 rot3 rot4 rot5 rot6])

;; With weights
(grid :cols [1 2 1]  ; middle column is twice as wide
      :rows [1 1]
      :children [...])

;; With auto-sizing
(grid :cols [1 :auto 1]  ; middle column uses preferred size
      :rows [1 :auto]
      :children [...])

;; With constraints
(grid :cols [{:weight 1 :min 50 :max 200}
             {:weight 2}]
      :rows 2
      :children [...])
```

### Nested Layouts

Combine layouts freely:

```clj
(stack [(text "Control Panel")
        (row [(vslider {:value 50})
              (vslider {:value 75})
              (vslider {:value 25})])
        (grid :cols 2 :rows 2 :children [...])])
```

## Styling

Components accept style maps that use Quil's drawing properties:

```clj
(hslider {:background {:fill [240 240 240]
                      :stroke [200 200 200]
                      :stroke-weight 2}
          :bar {:fill [100 200 150]
                :stroke-weight 0}
          :text {:fill [50 50 50]
                 :text-align :center
                 :text-size 14}})
```

Common style properties:
- `:fill` - Fill color
- `:stroke` - Stroke color
- `:stroke-weight` - Stroke thickness
- `:text-align` - `:left`, `:center`, `:right`
- `:text-size` - Font size

## Event Handling

### Mouse Events

Components can handle mouse events:

```clj
(def my-component
  (atom {:value 0}
        :meta {:-mouse-pressed (fn [this event]
                                 (println "Pressed at" (:x event) (:y event)))
                :-mouse-dragged (fn [this event]
                                  (swap! this update :value inc))}))
```

Available events:
- `:-mouse-pressed`
- `:-mouse-released`
- `:-mouse-moved`
- `:-mouse-dragged`
- `:-mouse-clicked`
- `:-mouse-entered`
- `:-mouse-exited`
- `:-mouse-wheel`

### Keyboard Events

Focusable components can receive keyboard input:

```clj
(def focusable-comp
  (atom {:index 0}
        :meta {:focusable? true
               :key-pressed-map {:up (fn [this]
                                       (swap! this update :index dec))
                                  :down (fn [this]
                                          (swap! this update :index inc))}}))
```

## Usage

### Basic Example

```clj
(ns plasticine-example
  (:require
   [casa.squid.plasticine :as p]
   [quil.core :as q]))

(def params
  [{:name "freq" :min 100 :max 1000 :value (atom 100)}])

(def sliders
  (for [{:keys [name min max value step]} params]
    (p/hslider {:min min
                :max max
                :step step
                :model value
                :on-change #(println name %)
                :height 60
                :format #(str name ": " %)})))

(def app
  (p/stack sliders :margin 4 :gap 4))

(q/defsketch controllers
  :title       ""
  :settings    #(q/smooth 2)
  :features    [:resizable :keep-on-top]
  :middleware  [p/middleware]
  ::p/root     #'app
  :size        [323 200]
  ::p/defaults {:text-size     25
                :frame-rate    30
                :stroke        [0 0 0]
                :fill          [0 0 0]
                :stroke-weight 15
                :background    [235 214 125]
                :rect-mode     :corner
                :stroke-cap    :round})
```

### Grid Example

```clj
(def knobs
  [(p/rotary {:value 25 :size 60})
   (p/rotary {:value 50 :size 60})
   (p/rotary {:value 75 :size 60})
   (p/rotary {:value 100 :size 60})])

(def app
  (p/stack [(p/text "Mixer Controls" {:text-size 20})
            (p/grid :cols 2 :rows 2 :children knobs)]
           :margin 20 :gap 15))
```

### Data Binding Example

```clj
(def state (atom {:volume 0.5 :pan 0.0 :filter 1000}))

(def controls
  (p/stack [(p/text "Audio Controls")
            (p/hslider {:model (get-in state [:volume])
                       :min 0 :max 1
                       :format #(str "Volume: " (int (* 100 %)) "%"})
            (p/hslider {:model (get-in state [:pan])
                       :min -1 :max 1
                       :format #(str "Pan: " %)})
            (p/rotary {:model (get-in state [:filter])
                      :min 100 :max 10000
                      :format #(str "Filter: " (int %) "Hz"})]))
```

<!-- license -->
## License

Copyright &copy; 2024 Arne Brasseur and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license -->
