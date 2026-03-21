(ns examples.components.select-list.basic-select-list
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.select-list :refer [select-list]]
   [casa.squid.plasticine.text :refer [text]]
   [quil.core :as q]))

;; Create text components for the select list
(def list-items
  [(p/text "Option 1" {:text-size 18})
   (p/text "Option 2" {:text-size 18})
   (p/text "Option 3" {:text-size 18})
   (p/text "Option 4" {:text-size 18})
   (p/text "Option 5" {:text-size 18})])

;; Create a basic select list
(def basic-select-list
  (select-list list-items))

(def app
  (p/stack [(p/text "Basic Select List" {:text-size 20})
            basic-select-list
            (p/text "Use up/down arrow keys to navigate")]
           :margin 20
           :gap 20))

(q/defsketch basic-select-list-example
  :title "Basic Select List Example"
  :size [300 300]
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