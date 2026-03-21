(ns simple-vslider-demo
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [vslider]]
   [quil.core :as q]))

;; Create a simple vertical slider
(def simple-vslider
  (vslider {:value 50
            :min 0
            :max 100
            :width 30
            :format #(str (int %))}))

;; Simple demo without complex layout
(def app simple-vslider)

(q/defsketch simple-vslider-demo
  :title "Simple Vertical Slider Demo"
  :size [100 300]
  :features [:resizable :keep-on-top]
  :middleware [p/middleware]
  ::p/root #'app
  :setup #(q/smooth 2)
  ::p/defaults {:text-size 16
                :frame-rate 30
                :stroke 0
                :fill 0
                :stroke-weight 1
                :background [240 240 240]
                :rect-mode :corner})