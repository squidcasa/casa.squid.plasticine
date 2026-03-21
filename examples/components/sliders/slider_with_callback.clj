(ns examples.components.sliders.slider-with-callback
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Sliders with on-change callbacks
(def hslider-with-callback
  (hslider {:value 30
            :min 0
            :max 100
            :height 30
            :on-change (fn [new-value]
                         (println "Horizontal slider changed to:" new-value))}))

(def vslider-with-callback
  (vslider {:value 60
            :min 0
            :max 100
            :width 30
            :on-change (fn [new-value]
                         (println "Vertical slider changed to:" new-value))}))

(def app
  (p/stack [(p/text "Slider with Callback" {:text-size 20})
            (p/cols [hslider-with-callback
                     vslider-with-callback]
                    :gap 30)
            (p/text "Check console for callback messages")]
           :margin 20
           :gap 20))

(q/defsketch slider-callback-example
  :title "Slider with Callback Example"
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