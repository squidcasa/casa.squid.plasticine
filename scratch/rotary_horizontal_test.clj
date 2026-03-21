(ns rotary-horizontal-test
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Create a single rotary knob to test horizontal movement
(def test-rotary
  (rotary {:value 50
           :min 0
           :max 100
           :size 100
           :background {:fill [200 220 255]
                       :stroke 0
                       :stroke-weight 2}
           :notch {:stroke 0
                   :stroke-weight 4}}))

(def app test-rotary)

(q/defsketch rotary-horizontal-test
  :title "Rotary Horizontal Movement Test"
  :size [200 200]
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