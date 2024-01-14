# plasticine

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/casa.squid/plasticine)](https://cljdoc.org/d/casa.squid/plasticine) [![Clojars Project](https://img.shields.io/clojars/v/casa.squid/plasticine.svg)](https://clojars.org/casa.squid/plasticine)
<!-- /badges -->

GUI Toolkit for Quil

## Features

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

## Usage

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
                :on-change #(ctl bell1 name %)
                :height 60
                :bar
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

<!-- license -->
## License

Copyright &copy; 2024 Arne Brasseur and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license -->
