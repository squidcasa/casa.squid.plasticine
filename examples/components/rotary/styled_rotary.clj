(ns examples.components.rotary.styled-rotary
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Styled rotary knob with custom colors and size
(def styled-rotary
  (rotary {:value 75
           :min 0
           :max 100
           :size 100
           :background {:fill [100 150 200]
                        :stroke [50 50 50]
                        :stroke-weight 3}
           :notch {:stroke [255 255 255]
                   :stroke-weight 5}}))

(def app
  (p/stack [(p/text "Styled Rotary Knob" {:text-size 20})
            styled-rotary
            (p/text "Custom colors and larger size")]
           :margin 20
           :gap 20))

(q/defsketch styled-rotary-example
  :title "Styled Rotary Knob Example"
  :size [350 350]
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