(ns examples.components.grid.constrained-grid
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Create some rotaries
(def rotaries
  [(rotary {:value 20 :size 40})
   (rotary {:value 40 :size 40})
   (rotary {:value 60 :size 40})
   (rotary {:value 80 :size 40})])

;; Create a grid with min/max constraints
(def constrained-grid
  (p/grid :cols [{:min 60 :max 120} {:min 60 :max 120}]  ; Constrain column sizes
          :rows [{:min 60 :max 120} {:min 60 :max 120}]  ; Constrain row sizes
          :children rotaries))

(def app
  (p/stack [(p/text "Grid with Min/Max Constraints" {:text-size 20})
            constrained-grid
            (p/text "Columns and rows have size constraints")]
           :margin 20
           :gap 20))

(q/defsketch constrained-grid-example
  :title "Constrained Grid Example"
  :size [300 300]
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