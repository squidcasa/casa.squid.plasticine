(ns casa.squid.plasticine.select-list
  "List with selected item and up/down arrow handling"
  (:require
   [casa.squid.plasticine.component :as c]
   [casa.squid.plasticine.draw :as d]
   [casa.squid.plasticine.object :as o]
   [casa.squid.plasticine.container :as container]
   [quil.applet :as ap]
   [quil.core :as q]))

(defn select-list-draw [{:keys [children index]} x y w h]
  (doseq [[idx [child x y w h]] (map list
                                     (range)
                                     (container/layout-stack children x y w h))]
    (when (= idx index)
      (d/with-props {:fill [190 190 250]
                     :stroke-weight 0}
        (q/rect x y w h)))
    (c/draw child x y w h)))

(def select-list-meta
  {:-draw        #'select-list-draw
   :-min-size    #'container/container-size
   :-pref-size   #'container/container-size
   :-max-size    #'container/container-size
   :focusable?   true
   :key-pressed-map
   {:down
    (fn [this]
      (swap! this update :index
             (fn [i]
               (min (inc i) (dec (count (:children @this))))))
      (o/dispatch this :on-index-changed (:index @this)))
    :up
    (fn [this]
      (swap! this update :index
             (fn [i]
               (max 0 (dec i))))
      (o/dispatch this :on-index-changed (:index @this)))}})

(defn select-list [children]
  (atom {:children   children
         :layout-f   container/layout-stack
         :index      0}
        :meta select-list-meta))
