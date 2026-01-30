(ns casa.squid.plasticine.grid
  (:require
   [casa.squid.plasticine.component :as c]))

(defn normalize-dimension [spec count]
  (cond
    (number? spec) (repeat count {:weight spec})
    (vector? spec) (map #(cond
                           (map? %)    %
                           (number? %) {:weight %}
                           (= % :auto) {:auto true}
                           :else       {:weight 1}) spec)
    (= spec :auto) (repeat count {:auto true})
    (nil? spec)    (repeat count {:weight 1})
    :else          (repeat count {:weight 1})))

(defn calculate-sizes [specs total-size]
  (let [specs-vec       (vec specs)
        auto-count      (count (filter :auto specs-vec))
        weight-specs    (vec (remove :auto specs-vec))
        weights         (map #(or (:weight %) 1) weight-specs)
        total-weight    (apply + weights)
        available-space (if (pos? auto-count)
                          (max 0 (- total-size (* auto-count 50))) ; Reserve 50px for each auto
                          total-size)
        weight-unit     (if (and (pos? total-weight) (pos? available-space))
                          (/ available-space total-weight)
                          (if (pos? (count weight-specs))
                            (/ total-size (count weight-specs))
                            1))
        auto-size       50] ; Default size for auto elements
    (mapv (fn [spec]
            (if (:auto spec)
              auto-size
              (max 1 (* (or (:weight spec) 1) weight-unit))))
          specs-vec)))

(defn grid-layout [{:keys [rows cols children] :as this} w h]
  (let [col-count (cond
                    (number? cols) cols
                    (vector? cols) (count cols)
                    (nil? cols) (max 1 (int (Math/ceil (Math/sqrt (count children)))))
                    :else (max 1 (int (Math/ceil (Math/sqrt (count children))))))
        row-count (cond
                    (number? rows) rows
                    (vector? rows) (count rows)
                    (nil? rows) (max 1 (int (Math/ceil (/ (count children) col-count))))
                    :else (max 1 (int (Math/ceil (/ (count children) col-count)))))
        col-specs (vec (normalize-dimension cols col-count))
        row-specs (vec (normalize-dimension rows row-count))
        col-sizes (vec (calculate-sizes col-specs w))
        row-sizes (vec (calculate-sizes row-specs h))
        col-positions (vec (reductions + 0 col-sizes))
        row-positions (vec (reductions + 0 row-sizes))
        total-cols (count col-sizes)
        total-rows (count row-sizes)]
    (for [[idx child] (map vector (range) children)
          :let [col-idx (mod idx col-count)
                row-idx (int (Math/floor (/ idx col-count)))
                x-pos (if (and (pos? total-cols) (< col-idx total-cols))
                        (nth col-positions col-idx 0)
                        (* col-idx (/ w col-count)))
                y-pos (if (and (pos? total-rows) (< row-idx total-rows))
                        (nth row-positions row-idx 0)
                        (* row-idx (/ h (max 1 row-count))))
                width (if (and (pos? total-cols) (< col-idx total-cols))
                        (nth col-sizes col-idx 0)
                        (/ w (max 1 col-count)))
                height (if (and (pos? total-rows) (< row-idx total-rows))
                         (nth row-sizes row-idx 0)
                         (/ h (max 1 row-count)))]
          :when (and (< col-idx col-count) (< row-idx row-count) (< idx (count children)))]
      [child x-pos y-pos width height])))

(defn grid-draw [{:keys [children] :as this} x y w h]
  (doseq [[child x' y' w' h'] (grid-layout this w h)]
    (c/draw child (+ x x') (+ y y') w' h')))

(defn grid-size [{:keys [children bounds] :as this}]
  (if bounds
    bounds
    [100 100]))

(def grid-meta
  {:-draw        #'grid-draw
   :-min-size    #'grid-size
   :-pref-size   #'grid-size
   :-max-size    #'grid-size
   :-mouse-event #'c/forward-mouse-event})

(defn grid [& {:as opts}]
  (atom (merge {:cols 1 :rows 1 :children []} opts) :meta grid-meta))

(comment
  ;; cols and rows, with maps as specs
  (grid :cols [{:weight 1} {:weight 2}]
        :rows [{:min 50 :max 150}
               {:auto true}])

  ;; shorthand:
  ;;;; numbers = explicit weights
  (grid :cols [1 1 2 0.5]
        :rows [1 1/3 3])

  ;; :auto = honor preferred size
  (grid :cols [1 :auto 1]
        :rows [1 :auto 2])

  (grid :cols 3 :rows 3)
  ;; same as
  (grid :cols [1 1 1] :rows [1 1 1])
  ;; same as
  (grid :cols [{:weight 1} {:weight 1} {:weight 1}]
        :rows [{:weight 1} {:weight 1} {:weight 1}])

  )
