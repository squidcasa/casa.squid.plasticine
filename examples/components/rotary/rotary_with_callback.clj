(ns examples.components.rotary.rotary-with-callback
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Rotary knob with on-change callback
(def callback-rotary
  (rotary {:value 40
           :min 0
           :max 100
           :size 80
           :on-change (fn [new-value]
                        (println "Rotary value changed to:" new-value))}))

(def app
  (p/stack [(p/text "Rotary with Callback" {:text-size 20})
            callback-rotary
            (p/text "Check console for callback messages")]
           :margin 20
           :gap 20))

(q/defsketch rotary-callback-example
  :title "Rotary with Callback Example"
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