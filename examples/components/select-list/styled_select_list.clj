(ns examples.components.select-list.styled-select-list
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.select-list :as select-list]
   [casa.squid.plasticine.text :as text]
   [casa.squid.plasticine.draw :as d]
   [quil.core :as q]))

;; Custom draw function for select list
(defn styled-select-list-draw [{:keys [children index]} x y w h]
  (doseq [[idx [child x y w h]] (map list
                                     (range)
                                     (select-list/layout-stack children x y w h))]
    (when (= idx index)
      (d/with-props {:fill [100 150 255]
                     :stroke [50 50 150]
                     :stroke-weight 2}
        (q/rect x y w h)))
    (p/draw child x y w h)))

;; Custom select list meta with styled draw function
(def styled-select-list-meta
  (assoc select-list/select-list-meta
         :-draw #'styled-select-list-draw))

;; Create text components for the select list
(def list-items
  [(p/text "Item 1" {:text-size 18})
   (p/text "Item 2" {:text-size 18})
   (p/text "Item 3" {:text-size 18})
   (p/text "Item 4" {:text-size 18})
   (p/text "Item 5" {:text-size 18})])

;; Create a styled select list
(def styled-select-list
  (atom {:children list-items
         :index 0}
        :meta styled-select-list-meta))

(def app
  (p/stack [(p/text "Styled Select List" {:text-size 20})
            styled-select-list
            (p/text "Custom styling for selected item")]
           :margin 20
           :gap 20))

(q/defsketch styled-select-list-example
  :title "Styled Select List Example"
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