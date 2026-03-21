(ns simple-rotary-test
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Create a single rotary knob
(def simple-rotary
  (rotary {:value 50
           :min 0
           :max 100
           :size 80
           :background {:fill [200 220 255]
                       :stroke 0
                       :stroke-weight 2}
           :notch {:stroke 0
                   :stroke-weight 4}}))

(def app
  (p/stack [(p/text "Simple Rotary Test" {:text-size 20})
            simple-rotary]
           :margin 20
           :gap 20))

(q/defsketch simple-rotary-test
  :title "Simple Rotary Test"
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