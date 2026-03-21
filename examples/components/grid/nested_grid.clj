(ns examples.components.grid.nested-grid
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Create inner grids
(def inner-grid-1
  (p/grid :cols 2
          :rows 1
          :children [(rotary {:value 30 :size 40})
                     (rotary {:value 70 :size 40})]))

(def inner-grid-2
  (p/grid :cols 1
          :rows 2
          :children [(hslider {:value 40 :min 0 :max 100 :height 20})
                     (hslider {:value 60 :min 0 :max 100 :height 20})]))

;; Create outer grid containing the inner grids
(def nested-grid
  (p/grid :cols 2
          :rows 2
          :children [inner-grid-1
                     (rotary {:value 50 :size 60})
                     (rotary {:value 80 :size 60})
                     inner-grid-2]))

(def app
  (p/stack [(p/text "Nested Grid Layouts" {:text-size 20})
            nested-grid
            (p/text "Grid containing other grids")]
           :margin 20
           :gap 20))

(q/defsketch nested-grid-example
  :title "Nested Grid Layouts Example"
  :size [400 400]
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