(ns casa.squid.plasticine.text
  "Basic component for drawing text"
  (:require
   [casa.squid.plasticine.draw :as d]
   [quil.core :as q]))

(defn text-draw [{:keys [text props]} x y w h]
  (let [tw (q/text-width text)
        th (d/prop :text-size 12)]
    (q/text text
            (max x (+ x (/ w 2) (- (/ tw 2))))
            (max y (+ y (/ h 2) (- (/ th 2))))
            w
            h)))

(defn text-min-size [{:keys [text]}]
  [(q/text-width text) (d/prop :text-size 12)])

(defn text-pref-size [{:keys [text]}]
  (let [text-size (d/prop :text-size 12)]
    [(+ (q/text-width text) text-size)
     (* 2 text-size)]))

(def text-meta
  {:-draw      #'text-draw
   :-min-size  #'text-min-size
   :-pref-size #'text-pref-size})

(defn text [text & {:as props}]
  (atom {:text text :props props}
        :meta text-meta))
