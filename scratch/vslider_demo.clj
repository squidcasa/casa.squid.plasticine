(ns vslider-demo
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [vslider hslider]]
   [quil.core :as q]))

;; Create a vertical slider
(def vertical-slider
  (vslider {:value 50
            :min 0
            :max 100
            :width 30
            :background {:fill [200 220 255]
                         :stroke 0
                         :stroke-weight 2}
            :bar {:fill [73 175 157]
                  :stroke-weight 0}
            :format #(str (int %))}))

;; Create a horizontal slider for comparison
(def horizontal-slider
  (hslider {:value 50
            :min 0
            :max 100
            :height 30
            :background {:fill [220 200 255]
                         :stroke 0
                         :stroke-weight 2}
            :bar {:fill [175 73 157]
                  :stroke-weight 0}
            :format #(str (int %))}))

(def app
  (p/stack [(p/text "Vertical Slider Demo" {:text-size 20})
            (p/cols [vertical-slider
                     (p/text "Vertical\nSlider" {:text-size 14 :text-align :center})
                     horizontal-slider
                     (p/text "Horizontal\nSlider" {:text-size 14 :text-align :center})]
                    :margin 20
                    :gap 20)]
          :margin 20
          :gap 20))

(q/defsketch vslider-demo
  :title "Vertical Slider Demo"
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