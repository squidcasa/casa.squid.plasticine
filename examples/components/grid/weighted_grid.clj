(ns examples.components.grid.weighted-grid
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Create components with different sizes
(def components
  [(rotary {:value 30 :size 60})
   (hslider {:value 70 :min 0 :max 100 :height 25})
   (rotary {:value 60 :size 60})
   (hslider {:value 40 :min 0 :max 100 :height 25})])

;; Create a grid with weighted columns
(def weighted-grid
  (p/grid :cols [2 1]  ; First column twice as wide as second
          :rows [1 1]  ; Equal heights
          :children components))

(def app
  (p/stack [(p/text "Weighted Grid Columns" {:text-size 20})
            weighted-grid
            (p/text "First column is twice as wide as second")]
           :margin 20
           :gap 20))

(q/defsketch weighted-grid-example
  :title "Weighted Grid Columns Example"
  :size [400 300]
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