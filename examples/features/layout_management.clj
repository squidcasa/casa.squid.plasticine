(ns examples.features.layout-management
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Create components
(def components
  [(rotary {:value 30 :size 50})
   (hslider {:value 50 :min 0 :max 100 :height 25})
   (vslider {:value 70 :min 0 :max 100 :width 25})
   (rotary {:value 90 :size 50})])

;; Different layout approaches
(def stack-layout
  (p/stack components :gap 10))

(def row-layout
  (p/rows components :gap 10))

(def column-layout
  (p/cols components :gap 10))

(def grid-layout
  (p/grid :cols 2 :rows 2 :children components))

(def app
  (p/stack [(p/text "Layout Management Example" {:text-size 20})
            (p/text "Different layout strategies")
            (p/cols [(p/stack [(p/text "Stack" {:text-size 16}) stack-layout] :gap 5)
                     (p/stack [(p/text "Row" {:text-size 16}) row-layout] :gap 5)
                     (p/stack [(p/text "Column" {:text-size 16}) column-layout] :gap 5)
                     (p/stack [(p/text "Grid" {:text-size 16}) grid-layout] :gap 5)]
                    :gap 20)]
           :margin 20
           :gap 20))

(q/defsketch layout-management-example
  :title "Layout Management Example"
  :size [800 400]
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