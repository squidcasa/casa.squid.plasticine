(ns examples.components.container.stack-layout
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Create components for the stack
(def components
  [(p/text "First Component" {:text-size 18})
   (rotary {:value 30 :size 60})
   (p/text "Second Component" {:text-size 18})
   (hslider {:value 70 :min 0 :max 100 :height 30})
   (p/text "Third Component" {:text-size 18})])

;; Create a stack layout
(def stack-layout
  (p/stack components
           :gap 15))

(def app
  (p/stack [(p/text "Stack Layout" {:text-size 20})
            stack-layout
            (p/text "Components stacked vertically with gaps")]
           :margin 20
           :gap 20))

(q/defsketch stack-layout-example
  :title "Stack Layout Example"
  :size [300 500]
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