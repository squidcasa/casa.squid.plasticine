(ns examples.components.container.column-layout
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Create components for the column
(def components
  [(p/text "Left" {:text-size 18})
   (rotary {:value 50 :size 60})
   (p/text "Middle" {:text-size 18})
   (hslider {:value 60 :min 0 :max 100 :height 30})
   (p/text "Right" {:text-size 18})])

;; Create a column layout
(def column-layout
  (p/cols components
          :gap 10))

(def app
  (p/stack [(p/text "Column Layout" {:text-size 20})
            column-layout
            (p/text "Components arranged vertically")]
           :margin 20
           :gap 20))

(q/defsketch column-layout-example
  :title "Column Layout Example"
  :size [200 500]
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
