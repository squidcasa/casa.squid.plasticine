(ns step-trace
  (:require [casa.squid.plasticine.grid :as grid]
            [casa.squid.plasticine.component :as c]))

(defn test-component [width height]
  (atom {:width width :height height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

(let [children [(test-component 50 30) (test-component 60 40)]
      grid (grid/grid :children children)]
  
  (println "=== Step by step trace ===")
  (let [this @grid
        child-count (count children)
        
        ;; Replicate the exact calculation from grid-layout-size
        col-count (cond
                    (number? (:cols this)) (:cols this)
                    (vector? (:cols this)) (count (:cols this))
                    (nil? (:cols this)) (max 1 (int (Math/ceil (Math/sqrt child-count))))
                    :else (max 1 (int (Math/ceil (Math/sqrt child-count)))))
        
        row-count (cond
                    (number? (:rows this)) (:rows this)
                    (vector? (:rows this)) (count (:rows this))
                    (nil? (:rows this)) (max 1 (int (Math/ceil (/ child-count col-count))))
                    :else (max 1 (int (Math/ceil (/ child-count col-count)))))]
    
    (println "Grid component:" this)
    (println "Child count:" child-count)
    (println "Cols:" (:cols this))
    (println "Rows:" (:rows this))
    (println "Calculated col-count:" col-count)
    (println "Calculated row-count:" row-count)
    
    ;; Check if there's a different calculation happening
    (let [sqrt-val (Math/sqrt child-count)
          ceil-val (int (Math/ceil sqrt-val))
          div-val (/ child-count ceil-val)
          final-col-count (max 1 ceil-val)
          final-row-count (max 1 (int (Math/ceil div-val)))]
      
      (println "\nDetailed calculation:")
      (println "  sqrt(2) =" sqrt-val)
      (println "  ceil(sqrt(2)) =" ceil-val)
      (println "  2/ceil(2) =" div-val)
      (println "  ceil(2/2) =" (int (Math/ceil div-val)))
      (println "  final col-count:" final-col-count)
      (println "  final row-count:" final-row-count)
      
      ;; Let's trace the actual calculation in grid-layout-size
      (let [constraints [0 Long/MAX_VALUE 0 Long/MAX_VALUE]
            [min-w max-w min-h max-h] constraints
            
            ;; Replicate the exact calculation
            grid-positions (for [child-idx (range child-count)]
                             {:child (nth children child-idx)
                              :col (mod child-idx final-col-count)
                              :row (int (Math/floor (/ child-idx final-col-count)))})
            
            children-by-col (group-by :col grid-positions)
            children-by-row (group-by :row grid-positions)
            
            col-sizes (for [col-idx (range final-col-count)]
                        (apply max 0 (map #(nth (c/layout-size (:child %) constraints) 0)
                                     (get children-by-col col-idx []))))
            
            row-sizes (for [row-idx (range final-row-count)]
                        (apply max 0 (map #(nth (c/layout-size (:child %) constraints) 1)
                                     (get children-by-row row-idx []))))
            
            total-width (apply + col-sizes)
            total-height (apply + row-sizes)
            
            final-width (min (max total-width min-w) max-w)
            final-height (min (max total-height min-h) max-h)]
        
        (println "\nGrid positions:")
        (doseq [[idx pos] (map-indexed vector grid-positions)]
          (println "  child" idx "-> col" (:col pos) "row" (:row pos)))
        
        (println "\nCol analysis:")
        (doseq [col-idx (range final-col-count)]
          (let [children-in-col (get children-by-col col-idx)
                sizes (map #(c/layout-size (:child %) constraints) children-in-col)
                widths (map first sizes)]
            (println (format "  col %d: children=%s sizes=%s widths=%s max=%d" 
                             col-idx (count children-in-col) sizes widths (apply max 0 widths)))))
        
        (println "\nRow analysis:")
        (doseq [row-idx (range final-row-count)]
          (let [children-in-row (get children-by-row row-idx)
                sizes (map #(c/layout-size (:child %) constraints) children-in-row)
                heights (map second sizes)]
            (println (format "  row %d: children=%s sizes=%s heights=%s max=%d" 
                             row-idx (count children-in-row) sizes heights (apply max 0 heights)))))
        
        (println "\nFinal calculation:")
        (println "  col-sizes:" col-sizes)
        (println "  row-sizes:" row-sizes)
        (println "  total-width:" total-width)
        (println "  total-height:" total-height)
        (println "  final-width:" final-width)
        (println "  final-height:" final-height)
        
        [final-width final-height])))