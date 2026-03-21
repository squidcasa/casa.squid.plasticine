(ns examples.components.container.nested-containers
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Create nested containers
(def inner-row
  (p/rows [(rotary {:value 30 :size 40})
           (p/text "R" {:text-size 16})
           (rotary {:value 70 :size 40})]
          :gap 5))

(def inner-column
  (p/cols [(p/text "T" {:text-size 16})
           (vslider {:value 50 :min 0 :max 100 :width 25})
           (p/text "B" {:text-size 16})]
          :gap 5))

;; Create outer stack with nested containers
(def nested-containers
  (p/stack [inner-row
            (hslider {:value 60 :min 0 :max 100 :height 25})
            inner-column]
           :gap 10))

(def app
  (p/stack [(p/text "Nested Containers" {:text-size 20})
            nested-containers
            (p/text "Combining different container types")]
           :margin 20
           :gap 20))

(q/defsketch nested-containers-example
  :title "Nested Containers Example"
  :size [300 400]
  :features [:resizable :keep-on-top]
  :middleware [p/middleware]
  ::p/root #'app
  :settings #(q/smooth 2)
  ::p/defaults {:text-size 16
                :frame-rate 30
                :stroke 0
                :fill 0
                :stroke-weight 1
                :background [240 240 240]
                :rect-mode :corner})