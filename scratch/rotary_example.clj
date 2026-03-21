(ns rotary-example
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Simple example showing a rotary knob
(def model (atom 50))

(def rotary-knob
  (rotary {:model model
           :min 0
           :max 100
           :size 80
           :background {:fill [200 200 200]
                        :stroke 0
                        :stroke-weight 2}
           :notch {:stroke 0
                   :stroke-weight 4}}))

(def app
  (p/stack [rotary-knob
            (p/text "Rotary Knob Example" {:text-size 20})]
           :margin 20 :gap 20))

(q/defsketch rotary-demo
  :title "Rotary Knob Demo"
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