(ns grid-test
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

;; Create a grid with explicit weights
(def test-grid
  (p/grid :cols [1 1]  ; Equal weights for columns
          :rows [1 1]  ; Equal weights for rows
          :children rotaries))

(def app
  (p/stack [(p/text "Grid Test" {:text-size 20})
            test-grid]
           :margin 20
           :gap 20))

(q/defsketch grid-test
  :title "Grid Test"
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