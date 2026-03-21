(ns examples.components.rotary.multiple-rotaries
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Multiple rotary knobs with different configurations
(def rotary1
  (rotary {:value 25
           :min 0
           :max 100
           :size 60
           :background {:fill [200 220 255]
                        :stroke 0
                        :stroke-weight 2}
           :notch {:stroke 0
                   :stroke-weight 3}}))

(def rotary2
  (rotary {:value 50
           :min 0
           :max 100
           :size 70
           :background {:fill [220 255 200]
                        :stroke [100 100 100]
                        :stroke-weight 2}
           :notch {:stroke [0 0 0]
                   :stroke-weight 4}}))

(def rotary3
  (rotary {:value 75
           :min 0
           :max 100
           :size 80
           :background {:fill [255 220 200]
                        :stroke 0
                        :stroke-weight 1}
           :notch {:stroke 0
                   :stroke-weight 5}}))

(def app
  (p/stack [(p/text "Multiple Rotary Knobs" {:text-size 20})
            (p/cols [rotary1 rotary2 rotary3]
                    :gap 30)
            (p/text "Three rotary knobs with different styles")]
           :margin 20
           :gap 20))

(q/defsketch multiple-rotaries-example
  :title "Multiple Rotary Knobs Example"
  :size [400 300]
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