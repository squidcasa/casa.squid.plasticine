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

(defn text-layout-size [{:keys [text]} [min-width max-width min-height max-height]]
  (let [text-size (d/prop :text-size 12)
        ;; Mock text-width for testing environment, fallback to real text-width
        text-width (try 
                     (q/text-width text)
                     (catch Exception e
                       ;; Fallback for testing - approximate width based on character count
                       (* 8 (count text))))
        preferred-width (+ text-width text-size)
        preferred-height (* 2 text-size)]
    [(min (max preferred-width min-width) max-width)
     (min (max preferred-height min-height) max-height)]))

(def text-meta
  {:-draw        #'text-draw
   :-layout-size #'text-layout-size})

(defn text [text & {:as props}]
  (atom {:text text :props props}
        :meta text-meta))
