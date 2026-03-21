(ns examples.components.select-list.select-list-callback
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.select-list :refer [select-list]]
   [casa.squid.plasticine.text :refer [text]]
   [quil.core :as q]))

;; Create text components for the select list
(def list-items
  [(p/text "Red" {:text-size 18})
   (p/text "Green" {:text-size 18})
   (p/text "Blue" {:text-size 18})
   (p/text "Yellow" {:text-size 18})
   (p/text "Purple" {:text-size 18})])

;; Selected item display
(def selected-display
  (p/text "Selected: None" {:text-size 18}))

;; Create select list with callback
(def select-list-with-callback
  (atom (assoc @select-list/select-list-meta
               :children list-items
               :index 0
               :on-index-changed (fn [index]
                                   (let [color (nth ["Red" "Green" "Blue" "Yellow" "Purple"] index)]
                                     (swap! selected-display assoc :text (str "Selected: " color))
                                     (println "Selected index changed to:" index))))
        :meta (assoc select-list/select-list-meta
                     :on-index-changed (fn [this index]
                                         (let [color (nth ["Red" "Green" "Blue" "Yellow" "Purple"] index)]
                                           (swap! selected-display assoc :text (str "Selected: " color))
                                           (println "Selected index changed to:" index))))))

(def app
  (p/stack [(p/text "Select List with Callback" {:text-size 20})
            select-list-with-callback
            selected-display
            (p/text "Use up/down arrow keys to navigate\nCheck console for callback messages")]
           :margin 20
           :gap 20))

(q/defsketch select-list-callback-example
  :title "Select List with Callback Example"
  :size [300 350]
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