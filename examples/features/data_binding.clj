(ns examples.features.data-binding
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [casa.squid.plasticine.object :as o]
   [quil.core :as q]))

;; Model for binding
(def model (atom 50))

;; Components bound to the same model
(def rotary-bound
  (rotary {:model model
           :min 0
           :max 100
           :size 80}))

(def hslider-bound
  (hslider {:model model
            :min 0
            :max 100
            :height 30}))

(def vslider-bound
  (vslider {:model model
            :min 0
            :max 100
            :width 30}))

;; Display the current value
(def value-display
  (p/text (str "Value: " @model) {:text-size 18}))

;; Update the display when the model changes
(add-watch model :update-display
           (fn [_ _ _ new-value]
             (swap! value-display assoc :text (str "Value: " (int new-value)))))

(def app
  (p/stack [(p/text "Data Binding Example" {:text-size 20})
            (p/text "All controls are bound to the same model")
            (p/cols [rotary-bound hslider-bound vslider-bound]
                    :gap 20)
            value-display
            (p/text "Changing any control updates all others")]
           :margin 20
           :gap 20))

(q/defsketch data-binding-example
  :title "Data Binding Example"
  :size [500 300]
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