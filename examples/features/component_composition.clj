(ns examples.features.component-composition
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Function to create a labeled control
(defn labeled-control [label component]
  (p/stack [(p/text label {:text-size 16})
            component]
           :gap 5))

;; Create some labeled controls
(def labeled-rotary
  (labeled-control "Volume"
                   (rotary {:value 75
                            :min 0
                            :max 100
                            :size 70})))

(def labeled-slider
  (labeled-control "Brightness"
                   (hslider {:value 60
                             :min 0
                             :max 100
                             :height 25})))

;; Group controls together
(def control-group
  (p/cols [labeled-rotary labeled-slider]
          :gap 40))

(def app
  (p/stack [(p/text "Component Composition Example" {:text-size 20})
            (p/text "Building complex UIs from simpler components")
            control-group]
           :margin 20
           :gap 20))

(q/defsketch component-composition-example
  :title "Component Composition Example"
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