(ns examples.components.rotary.rotary-with-model
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [quil.core :as q]))

;; Rotary knob with model binding
(def model (atom 30))

(def rotary-with-model
  (rotary {:model model
           :min 0
           :max 100
           :size 80}))

;; Display the current value
(def value-display
  (p/text (str "Value: " @model) {:text-size 18}))

;; Update the display when the model changes
(add-watch model :update-display
           (fn [_ _ _ new-value]
             (swap! value-display assoc :text (str "Value: " new-value))))

(def app
  (p/stack [(p/text "Rotary with Model Binding" {:text-size 20})
            rotary-with-model
            value-display
            (p/text "The value display updates automatically")]
           :margin 20
           :gap 20))

(q/defsketch rotary-model-example
  :title "Rotary with Model Binding Example"
  :size [300 350]
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