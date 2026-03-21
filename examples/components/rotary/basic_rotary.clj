(ns examples.components.rotary.basic-rotary
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Basic rotary knob example with default styling
(def basic-rotary
  (rotary {:value 50
           :min 0
           :max 100
           :size 80}))

(def app
  (p/stack [(p/text "Basic Rotary Knob" {:text-size 20})
            basic-rotary
            (p/text "Simple rotary knob with default styling")]
           :margin 20
           :gap 20))

(q/defsketch basic-rotary-example
  :title "Basic Rotary Knob Example"
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