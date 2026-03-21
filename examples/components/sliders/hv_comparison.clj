(ns examples.components.sliders.hv-comparison
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Horizontal slider
(def horizontal-slider
  (hslider {:value 50
            :min 0
            :max 100
            :height 30
            :format #(str "Value: " (int %))}))

;; Vertical slider
(def vertical-slider
  (vslider {:value 50
            :min 0
            :max 100
            :width 30
            :format #(str "Value: " (int %))}))

(def app
  (p/stack [(p/text "Horizontal vs Vertical Sliders" {:text-size 20})
            horizontal-slider
            vertical-slider
            (p/text "Comparison of horizontal and vertical sliders")]
           :margin 20
           :gap 20))

(q/defsketch hv-comparison-example
  :title "Horizontal vs Vertical Slider Comparison"
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
