(ns examples.features.custom-styling
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [casa.squid.plasticine.draw :as d]
   [quil.core :as q]))

;; Custom styled components
(def custom-rotary
  (rotary {:value 60
           :min 0
           :max 100
           :size 90
           :background {:fill [180 220 255]
                        :stroke [50 100 150]
                        :stroke-weight 3}
           :notch {:stroke [255 50 50]
                   :stroke-weight 4}}))

(def custom-hslider
  (hslider {:value 40
            :min 0
            :max 100
            :height 40
            :background {:fill [220 255 180]
                         :stroke [100 150 50]
                         :stroke-weight 2}
            :bar {:fill [255 100 100]
                  :stroke-weight 0}
            :format #(str "H: " (int %))}))

(def custom-vslider
  (vslider {:value 80
            :min 0
            :max 100
            :width 40
            :background {:fill [255 220 180]
                         :stroke [150 100 50]
                         :stroke-weight 2}
            :bar {:fill [100 255 100]
                  :stroke-weight 0}
            :format #(str "V: " (int %))}))

(def app
  (p/stack [(p/text "Custom Styling Example" {:text-size 20})
            (p/text "Components with custom colors and styling")
            (p/cols [custom-rotary custom-hslider custom-vslider]
                    :gap 25)]
           :margin 20
           :gap 20))

(q/defsketch custom-styling-example
  :title "Custom Styling Example"
  :size [600 300]
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