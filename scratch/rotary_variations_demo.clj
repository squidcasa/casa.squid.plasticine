(ns rotary-variations-demo
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Create several rotary knobs with different configurations
(def rotary1
  (rotary {:value 25
           :min 0
           :max 100
           :size 70
           :background {:fill [200 220 255]
                       :stroke 0
                       :stroke-weight 2}
           :notch {:stroke 0
                   :stroke-weight 4}}))

(def rotary2
  (rotary {:value 75
           :min 0
           :max 100
           :size 90
           :background {:fill [255 200 220]
                       :stroke [100 100 100]
                       :stroke-weight 3}
           :notch {:stroke [255 0 0]
                   :stroke-weight 5}}))

(def rotary3
  (rotary {:value 50
           :min 0
           :max 100
           :size 110
           :background {:fill [220 255 200]
                       :stroke 0
                       :stroke-weight 1}
           :notch {:stroke 0
                   :stroke-weight 6}}))

;; Arrange them in a vertical stack
(def app
  (p/stack [(p/text "Rotary Knob Variations" {:text-size 24})
            (p/text "Different sizes, colors, and styles" {:text-size 16})
            rotary1
            (p/text "Small blue knob")
            rotary2
            (p/text "Medium red knob with border")
            rotary3
            (p/text "Large green knob")]
           :margin 20
           :gap 15))

(q/defsketch rotary-variations-demo
  :title "Rotary Knob Variations Demo"
  :size [300 800]
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