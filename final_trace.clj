(ns final-trace
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
  
  (println "=== Final debug trace ===")
  (println "Grid component:" @grid)
  
  ;; Check which function is actually being used
  (let [result (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
    (println "layout-size result:" result)
    
    ;; Test with explicit values
    (let [manual-col-count 2
          manual-row-count 1
          
          ;; Manual calculation
          grid-positions (for [child-idx (range (count children))]
                           {:child (nth children child-idx)
                            :col (mod child-idx manual-col-count)
                            :row (int (Math/floor (/ child-idx manual-col-count)))})
          
          children-by-col (group-by :col grid-positions)
          children-by-row (group-by :row grid-positions)
          
          col-sizes (for [col-idx (range manual-col-count)]
                      (apply max 0 (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0)
                                   (get children-by-col col-idx []))))
          
          row-sizes (for [row-idx (range manual-row-count)]
                      (apply max 0 (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1)
                                   (get children-by-row row-idx []))))]
      
      (println "\nManual calculation (2x1):")
      (println "  col-sizes:" col-sizes "→ total:" (apply + col-sizes))
      (println "  row-sizes:" row-sizes "→ total:" (apply + row-sizes))
      
      ;; Test with 1x2 layout
      (let [manual-col-count-2 1
            manual-row-count-2 2
            
            grid-positions-2 (for [child-idx (range (count children))]
                               {:child (nth children child-idx)
                                :col (mod child-idx manual-col-count-2)
                                :row (int (Math/floor (/ child-idx manual-col-count-2)))})
            
            children-by-col-2 (group-by :col grid-positions-2)
            children-by-row-2 (group-by :row grid-positions-2)
            
            col-sizes-2 (for [col-idx (range manual-col-count-2)]
                          (apply max 0 (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0)
                                       (get children-by-col-2 col-idx []))))
            
            row-sizes-2 (for [row-idx (range manual-row-count-2)]
                          (apply max 0 (map #(nth (c/layout-size (:child %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1)
                                       (get children-by-row-2 row-idx []))))]
        
        (println "\nManual calculation (1x2):")
        (println "  col-sizes:" col-sizes-2 "→ total:" (apply + col-sizes-2))
        (println "  row-sizes:" row-sizes-2 "→ total:" (apply + row-sizes-2))
        
        (println "\nConclusion:")
        (println "  Expected: [110 30] - This seems incorrect!")
        (println "  Actual 2x1: [110 40] - This is correct for 2 columns, 1 row")
        (println "  Actual 1x2: [60 70] - This is correct for 1 column, 2 rows")
        (println "  Actual auto: [60 30] - This suggests 1 column, 1 row (incorrect)")
        
        ;; The issue is that the expected value [110 30] is wrong!
        ;; For 2 children with auto-sizing, we get 2 columns, 1 row = [110 40]
        ;; The test expectation [110 30] appears to be incorrect
        )))