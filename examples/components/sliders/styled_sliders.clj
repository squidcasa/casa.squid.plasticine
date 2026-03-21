(ns examples.components.sliders.styled-sliders
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Styled horizontal slider
(def styled-hslider
  (hslider {:value 75
            :min 0
            :max 100
            :height 40
            :background {:fill [220 200 255]
                         :stroke [100 100 100]
                         :stroke-weight 3}
            :bar {:fill [175 73 157]
                  :stroke-weight 0}
            :format #(str "H: " (int %))}))

;; Styled vertical slider
(def styled-vslider
  (vslider {:value 25
            :min 0
            :max 100
            :width 40
            :background {:fill [200 255 200]
                         :stroke [100 100 100]
                         :stroke-weight 3}
            :bar {:fill [73 175 157]
                  :stroke-weight 0}
            :format #(str "V: " (int %))}))

(def app
  (p/stack [(p/text "Styled Sliders" {:text-size 20})
            (p/cols [styled-hslider
                     styled-vslider]
                    :gap 50)
            (p/text "Custom colors, sizes, and value formatting")]
           :margin 20
           :gap 20))

(q/defsketch styled-sliders-example
  :title "Styled Sliders Example"
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