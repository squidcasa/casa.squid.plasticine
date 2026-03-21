(ns examples.components.sliders.basic-vslider
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [vslider]]
   [quil.core :as q]))

;; Basic vertical slider with default styling
(def basic-vslider
  (vslider {:value 50
            :min 0
            :max 100
            :width 30}))

(def app
  (p/stack [(p/text "Basic Vertical Slider" {:text-size 20})
            basic-vslider
            (p/text "Simple vertical slider with default styling")]
           :margin 20
           :gap 20))

(q/defsketch basic-vslider-example
  :title "Basic Vertical Slider Example"
  :size [200 400]
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