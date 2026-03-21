(ns rotary-grid-demo
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Create a grid of rotary knobs with different configurations
(def rotaries
  (for [i (range 9)]
    (rotary {:value (* i 12.5)  ; Values from 0 to 100 in steps of 12.5
             :min 0
             :max 100
             :size 60
             :background {:fill (if (even? i)
                                 [180 180 220]  ; Light blue for even indices
                                 [220 180 180]) ; Light red for odd indices
                         :stroke 0
                         :stroke-weight 2}
             :notch {:stroke 0
                     :stroke-weight (if (< i 5) 3 5)}}))) ; Different notch thicknesses

;; Create a grid layout using the grid component with explicit weights
(def rotary-grid
  (p/grid :cols [1 1 1]  ; Equal weights for columns
          :rows [1 1 1]  ; Equal weights for rows
          :children (vec rotaries)
          :gap 10))

;; Add a title
(def app
  (p/stack [(p/text "Rotary Knob Grid Demo" {:text-size 24})
            (p/text "Evenly distributed rotaries" {:text-size 16})
            rotary-grid]
           :margin 20
           :gap 20))

(q/defsketch rotary-grid-demo
  :title "Rotary Knob Grid Demo"
  :size [500 600]
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
