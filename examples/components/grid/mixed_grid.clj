(ns examples.components.grid.mixed-grid
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [casa.squid.plasticine.text :refer [text]]
   [quil.core :as q]))

;; Create a variety of components
(def mixed-components
  [(rotary {:value 25 :size 50})
   (hslider {:value 50 :min 0 :max 100 :height 25})
   (vslider {:value 75 :min 0 :max 100 :width 25})
   (p/text "Text Component")
   (rotary {:value 60 :size 50})
   (hslider {:value 30 :min 0 :max 100 :height 25})])

;; Create a grid with mixed components
(def mixed-grid
  (p/grid :cols 3
          :rows 2
          :children mixed-components))

(def app
  (p/stack [(p/text "Mixed Component Grid" {:text-size 20})
            mixed-grid
            (p/text "Grid containing different types of components")]
           :margin 20
           :gap 20))

(q/defsketch mixed-grid-example
  :title "Mixed Component Grid Example"
  :size [500 300]
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