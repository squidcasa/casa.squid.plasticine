(ns casa.squid.plasticine.container
  (:require
   [casa.squid.plasticine.component :as c]))

(defn container-draw [{:keys [children layout-f] :as this} x y w h]
  (doseq [[child x y w h] (layout-f this x y w h)]
    (c/draw child x y w h)))

(defn layout-rows [{:keys [children]} x y w h]
  (let [rh (/ h (count children))]
    (for [[idx child] (map vector (range) children)]
      [child x (+ y (* idx rh)) w rh])))

(defn layout-cols [{:keys [children]} x y w h]
  (let [cw (/ w (count children))]
    (for [[idx child] (map vector (range) children)]
      [child (+ x (* idx cw)) y cw h])))

(defn layout-stack [{:keys [children gap]
                     :or {gap 0}} x y w h]
  (second
   (reduce (fn [[yy res] ch]
             (let [[pw ph] (c/pref-size ch)]
               (if (<= (+ yy ph) h)
                 [(+ yy ph gap)
                  (conj res [ch x yy (min pw w) ph])]
                 (reduced [nil res]))))
           [y []] children)))

(defn container-size [{:keys [children layout-f bounds] :as this}]
  (if-let [[x y w h] bounds]
    (let [layout (layout-f this x y w h)
          x (apply min (map (fn [_ x _ _ _] x) layout))
          y (apply min (map (fn [_ _ y _ _] y) layout))]
      [(- (apply max (map (fn [_ x _ w _] (+ x w)) layout)) x)
       (- (apply max (map (fn [_ _ y _ h] (+ y h)) layout)) y)])
    [Long/MAX_VALUE Long/MAX_VALUE]))

(def container-meta
  {:-draw        #'container-draw
   :-min-size    #'container-size
   :-pref-size   #'container-size
   :-max-size    #'container-size
   :-mouse-event #'c/forward-mouse-event})

(defn container [children layout-f props]
  (atom (merge {:children children
                :layout-f layout-f}
               props)
        :meta container-meta))

(defn rows [children & {:as props}] (container children layout-rows props))
(defn cols [children & {:as props}] (container children layout-cols props))
(defn stack [children & {:as props}] (container children layout-stack props))
