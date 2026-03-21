(ns examples.components.sliders.slider-with-model
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Model for binding
(def model (atom 40))

;; Sliders with model binding
(def hslider-with-model
  (hslider {:model model
            :min 0
            :max 100
            :height 30}))

(def vslider-with-model
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
             (swap! value-display assoc :text (str "Value: " new-value))))

(def app
  (p/stack [(p/text "Slider with Model Binding" {:text-size 20})
            (p/cols [hslider-with-model
                     vslider-with-model]
                    :gap 30)
            value-display
            (p/text "Both sliders control the same model")]
           :margin 20
           :gap 20))

(q/defsketch slider-model-example
  :title "Slider with Model Binding Example"
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