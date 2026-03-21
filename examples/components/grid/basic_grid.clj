(ns examples.components.grid.basic-grid
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Create a few simple rotaries
(def rotaries
  [(rotary {:value 25 :size 50})
   (rotary {:value 50 :size 50})
   (rotary {:value 75 :size 50})
   (rotary {:value 100 :size 50})])

;; Create a basic grid
(def basic-grid
  (p/grid :cols 2  ; 2 columns
          :rows 2  ; 2 rows
          :children rotaries))

(def app
  (p/stack [(p/text "Basic Grid Layout" {:text-size 20})
            basic-grid
            (p/text "2x2 grid with rotary knobs")]
           :margin 20
           :gap 20))

(q/defsketch basic-grid-example
  :title "Basic Grid Layout Example"
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