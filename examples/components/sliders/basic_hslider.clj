(ns examples.components.sliders.basic-hslider
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Basic horizontal slider with default styling
(def basic-hslider
  (hslider {:value 50
            :min 0
            :max 100
            :height 30}))

(def app
  (p/stack [(p/text "Basic Horizontal Slider" {:text-size 20})
            basic-hslider
            (p/text "Simple horizontal slider with default styling")]
           :margin 20
           :gap 20))

(q/defsketch basic-hslider-example
  :title "Basic Horizontal Slider Example"
  :size [400 200]
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