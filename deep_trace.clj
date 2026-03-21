(ns deep-trace
  (:require [casa.squid.plasticine.grid :as grid]
            [casa.squid.plasticine.component :as c]))

(defn test-component [width height]
  (atom {:width width :height height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

(let [children [(test-component 50 30) (test-component 60 40)]
      this {:children children}]  
  
  (println "=== Deep trace of grid-layout-size ===")
  (let [child-count (count children)
        
        ;; Calculate column/row counts
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
    
    (println "Input:")
    (println "  children:" child-count)
    (println "  cols:" (:cols this))
    (println "  rows:" (:rows this))
    (println "  calculated col-count:" col-count)
    (println "  calculated row-count:" row-count)
    
    ;; Calculate sizes
    (let [grid-positions (for [child-idx (range child-count)]
                           {:child (nth children child-idx)
                            :col (mod child-idx col-count)
                            :row (int (Math/floor (/ child-idx col-count)))})
          
          children-by-col (group-by :col grid-positions)
          children-by-row (group-by :row grid-positions)
          
          col-sizes (for [col-idx (range col-count)]
                      (apply max 0 (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0)
                                   (get children-by-col col-idx []))))
          
          row-sizes (for [row-idx (range row-count)]
                      (apply max 0 (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1)
                                   (get children-by-row row-idx []))))]
      
      (println "\nGrid positions:")
      (doseq [[idx pos] (map-indexed vector grid-positions)]
        (println "  child" idx "-> col:" (:col pos) "row:" (:row pos)))
      
      (println "\nColumn analysis:")
      (doseq [col-idx (range col-count)]
        (let [children-in-col (get children-by-col col-idx)
              widths (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0) children-in-col)
              max-width (apply max 0 widths)]
          (println (format "  col %d: children=%s widths=%s max=%d" 
                           col-idx (count children-in-col) widths max-width))))
      
      (println "\nRow analysis:")
      (doseq [row-idx (range row-count)]
        (let [children-in-row (get children-by-row row-idx)
              heights (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1) children-in-row)
              max-height (apply max 0 heights)]
          (println (format "  row %d: children=%s heights=%s max=%d" 
                           row-idx (count children-in-row) heights max-height))))
      
      (println "\nFinal calculation:")
      (println "  col-sizes:" col-sizes)
      (println "  row-sizes:" row-sizes)
      (println "  total-width:" (apply + col-sizes))
      (println "  total-height:" (apply + row-sizes)))))