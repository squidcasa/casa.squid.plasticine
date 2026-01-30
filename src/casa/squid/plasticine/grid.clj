(ns casa.squid.plasticine.grid
  "Grid layout component for arranging children in a grid structure.

  Supports flexible column and row specifications including weights, auto-sizing,
  and min/max constraints."
  (:require
   [casa.squid.plasticine.component :as c]))

;; WARNING: An LLM generated this namespace. Some things may not make sense,
;; contain numerous bugs, and generally be frustrating to work with. Tread
;; carefully.

(defn normalize-dimension
  "Normalize dimension specification to a sequence of maps.

  Converts various specification formats to a uniform format:
  - Numbers become {:weight n}
  - :auto becomes {:auto true}
  - Maps are left as-is
  - nil becomes {:weight 1}"
  [spec count]
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

(defn calculate-sizes [specs total-size children axis col-count]
  (let [specs-seq (normalize-dimension specs col-count)
        auto-count (clojure.core/count (filter :auto specs-seq))
        weight-specs (remove :auto specs-seq)
        weights (map #(or (:weight %) 1) weight-specs)
        total-weight (apply + weights)
        ;; For auto specs, get preferred sizes from children
        auto-sizes (when (pos? auto-count)
                     (map-indexed
                       (fn [idx spec]
                         (if (:auto spec)
                           (let [child (nth children idx nil)]
                             (if (and child (fn? c/pref-size))
                               (nth (c/pref-size child) (if (= axis :cols) 0 1) 50)
                               50))
                           nil))
                       specs-seq))
        auto-total (if auto-sizes (apply + (remove nil? auto-sizes)) 0)
        available-space (clojure.core/max 0 (- total-size auto-total))
        weight-unit (if (and (pos? total-weight) (pos? available-space))
                      (/ available-space total-weight)
                      (if (pos? (clojure.core/count weight-specs))
                        (/ total-size (clojure.core/count weight-specs))
                        1))]
    (map-indexed (fn [idx spec]
                   (cond
                     (:auto spec) (or (nth auto-sizes idx nil) 50)
                     :else (clojure.core/max 1 (* (or (:weight spec) 1) weight-unit))))
                 specs-seq)))

(defn apply-min-max [size spec]
  (let [min-size (:min spec)
        max-size (:max spec)]
    (cond-> size
      min-size (clojure.core/max min-size)
      max-size (clojure.core/min max-size))))

(defn grid-layout
  "Calculate layout positions for grid children.

  Returns a sequence of [child x y width height] for each child."
  [{:keys [rows cols children] :as this} w h]
  (let [col-count (cond
                    (number? cols) cols
                    (vector? cols) (clojure.core/count cols)
                    (nil? cols)    (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children)))))
                    :else          (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children))))))
        row-count (cond
                    (number? rows) rows
                    (vector? rows) (clojure.core/count rows)
                    (nil? rows)    (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count))))
                    :else          (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count)))))
        col-specs (normalize-dimension cols col-count)
        row-specs (normalize-dimension rows row-count)
        col-sizes (calculate-sizes cols w children :cols col-count)
        row-sizes (calculate-sizes rows h children :rows row-count)
        col-sizes-with-constraints (map apply-min-max col-sizes col-specs)
        row-sizes-with-constraints (map apply-min-max row-sizes row-specs)
        col-positions (reductions + 0 col-sizes-with-constraints)
        row-positions (reductions + 0 row-sizes-with-constraints)
        total-cols (clojure.core/count col-sizes-with-constraints)
        total-rows (clojure.core/count row-sizes-with-constraints)]
    (for [[idx child] (map vector (range) children)
          :let [col-idx (mod idx col-count)
                row-idx (int (Math/floor (/ idx col-count)))
                x-pos (if (and (pos? total-cols) (< col-idx total-cols))
                        (nth col-positions col-idx 0)
                        (* col-idx (/ w col-count)))
                y-pos (if (and (pos? total-rows) (< row-idx total-rows))
                        (nth row-positions row-idx 0)
                        (* row-idx (/ h (clojure.core/max 1 row-count))))
                width (if (and (pos? total-cols) (< col-idx total-cols))
                        (nth col-sizes-with-constraints col-idx 0)
                        (/ w (clojure.core/max 1 col-count)))
                height (if (and (pos? total-rows) (< row-idx total-rows))
                         (nth row-sizes-with-constraints row-idx 0)
                         (/ h (clojure.core/max 1 row-count)))]
          :when (and (< col-idx col-count) (< row-idx row-count) (< idx (clojure.core/count children)))]
      [child x-pos y-pos width height])))

(defn grid-draw
  "Draw the grid component and its children."
  [{:keys [children] :as this} x y w h]
  (doseq [[child x' y' w' h'] (grid-layout this w h)]
    (c/draw child (+ x x') (+ y y') w' h')))

(defn grid-min-size
  "Calculate minimum size required for the grid."
  [{:keys [rows cols children] :as this}]
  (if-let [bounds (:bounds this)]
    bounds
    (let [col-count (cond
                      (number? cols) cols
                      (vector? cols) (clojure.core/count cols)
                      (nil? cols)    (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children)))))
                      :else          (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children))))))
          row-count (cond
                      (number? rows) rows
                      (vector? rows) (clojure.core/count rows)
                      (nil? rows)    (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count))))
                      :else          (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count)))))
          ;; Organize children into grid positions
          grid-positions (for [child-idx (range (clojure.core/count children))]
                           {:child (nth children child-idx)
                            :col (mod child-idx col-count)
                            :row (int (Math/floor (/ child-idx col-count)))})
          ;; Group children by column and row
          children-by-col (clojure.core/group-by :col grid-positions)
          children-by-row (clojure.core/group-by :row grid-positions)
          ;; For min size, sum the max min size in each column/row
          col-min-sizes (for [col-idx (range col-count)]
                          (apply clojure.core/max 0 (map #(nth (c/min-size (:child %)) 0 0)
                                           (get children-by-col col-idx []))))
          row-min-sizes (for [row-idx (range row-count)]
                          (apply clojure.core/max 0 (map #(nth (c/min-size (:child %)) 1 0)
                                           (get children-by-row row-idx []))))
          total-min-width (apply + col-min-sizes)
          total-min-height (apply + row-min-sizes)]
      [total-min-width total-min-height])))

(defn grid-pref-size
  "Calculate preferred size for the grid."
  [{:keys [rows cols children] :as this}]
  (if-let [bounds (:bounds this)]
    bounds
    (let [col-count (cond
                      (number? cols) cols
                      (vector? cols) (clojure.core/count cols)
                      (nil? cols)    (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children)))))
                      :else          (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children))))))
          row-count (cond
                      (number? rows) rows
                      (vector? rows) (clojure.core/count rows)
                      (nil? rows)    (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count))))
                      :else          (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count)))))
          ;; Organize children into grid positions
          grid-positions (for [child-idx (range (clojure.core/count children))]
                           {:child (nth children child-idx)
                            :col (mod child-idx col-count)
                            :row (int (Math/floor (/ child-idx col-count)))})
          ;; Group children by column and row
          children-by-col (clojure.core/group-by :col grid-positions)
          children-by-row (clojure.core/group-by :row grid-positions)
          ;; For preferred size, sum the max preferred size in each column/row
          col-pref-sizes (for [col-idx (range col-count)]
                           (apply clojure.core/max 0 (map #(nth (c/pref-size (:child %)) 0 100)
                                            (get children-by-col col-idx []))))
          row-pref-sizes (for [row-idx (range row-count)]
                           (apply clojure.core/max 0 (map #(nth (c/pref-size (:child %)) 1 100)
                                            (get children-by-row row-idx []))))
          total-pref-width (apply + col-pref-sizes)
          total-pref-height (apply + row-pref-sizes)]
      [total-pref-width total-pref-height])))

(defn grid-max-size
  "Calculate maximum size for the grid."
  [{:keys [rows cols children] :as this}]
  (if-let [bounds (:bounds this)]
    bounds
    (let [col-count (cond
                      (number? cols) cols
                      (vector? cols) (clojure.core/count cols)
                      (nil? cols)    (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children)))))
                      :else          (clojure.core/max 1 (int (Math/ceil (Math/sqrt (clojure.core/count children))))))
          row-count (cond
                      (number? rows) rows
                      (vector? rows) (clojure.core/count rows)
                      (nil? rows)    (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count))))
                      :else          (clojure.core/max 1 (int (Math/ceil (/ (clojure.core/count children) col-count)))))
          ;; For max size, we typically return large values unless there are fixed constraints
          ;; If rows/cols have max constraints, we respect those
          col-specs (normalize-dimension cols col-count)
          row-specs (normalize-dimension rows row-count)
          has-max-constraints (or (clojure.core/some :max col-specs) (clojure.core/some :max row-specs))]
      (if has-max-constraints
        ;; If there are max constraints, calculate based on those
        (let [grid-positions (for [child-idx (range (clojure.core/count children))]
                               {:child (nth children child-idx)
                                :col (mod child-idx col-count)
                                :row (int (Math/floor (/ child-idx col-count)))})
              children-by-col (clojure.core/group-by :col grid-positions)
              children-by-row (clojure.core/group-by :row grid-positions)
              col-max-sizes (for [col-idx (range col-count)]
                              (apply clojure.core/max 1 (map #(nth (c/max-size (:child %)) 0 Long/MAX_VALUE)
                                               (get children-by-col col-idx []))))
              row-max-sizes (for [row-idx (range row-count)]
                              (apply clojure.core/max 1 (map #(nth (c/max-size (:child %)) 1 Long/MAX_VALUE)
                                               (get children-by-row row-idx []))))
              total-max-width (apply + col-max-sizes)
              total-max-height (apply + row-max-sizes)]
          [total-max-width total-max-height])
        ;; Otherwise return large values
        [Long/MAX_VALUE Long/MAX_VALUE]))))

(def grid-meta
  {:-draw        #'grid-draw
   :-min-size    #'grid-min-size
   :-pref-size   #'grid-pref-size
   :-max-size    #'grid-max-size
   :-mouse-event #'c/forward-mouse-event})

(defn grid
  "Create a grid component with the specified options.

  Options:
  :cols - Column specifications (number, vector, or :auto)
  :rows - Row specifications (number, vector, or :auto)
  :children - Child components to arrange in the grid"
  [& {:as opts}]
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
