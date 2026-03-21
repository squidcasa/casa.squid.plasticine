;; WARNING: An LLM generated this namespace. Some things may not make sense,
;; contain numerous bugs, and generally be frustrating to work with. Tread carefully.

(ns debug.auto-sizing-debug
  (:require [casa.squid.plasticine.grid :as grid]
            [casa.squid.plasticine.component :as c]))

;; Create test components matching the failing test
(defn test-component [width height]
  (atom {:width width :height height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

;; Test case: 2 children [(test-component 50 30) (test-component 60 40)]
(def test-children
  [(test-component 50 30)
   (test-component 60 40)])

;; Create grid with auto-sizing (no explicit rows/cols)
(def auto-grid
  (grid/grid :children test-children))

(defn debug-auto-sizing []
  (println "=== Auto-sizing Grid Debug ===")
  (println "Test case: 2 children [(test-component 50 30) (test-component 60 40)]")
  (println "Expected: [110 30]")
  
  (let [constraints [0 Long/MAX_VALUE 0 Long/MAX_VALUE]
        result (c/layout-size auto-grid constraints)]
    
    (println (format "Actual result: %s" result))
    (println (format "Match expected? %s" (= result [110 30])))
    
    ;; Let's trace the calculation step by step
    (println "\n=== Step-by-step Calculation ===")
    
    ;; 1. Calculate column/row counts for auto-sizing
    (let [children test-children
          child-count (count children)
          
          ;; Auto-sizing calculation from grid.clj
          col-count (max 1 (int (Math/ceil (Math/sqrt child-count))))
          row-count (max 1 (int (Math/ceil (/ child-count col-count))))]
      
      (println (format "1. Children count: %d" child-count))
      (println (format "2. Auto column count: %d (sqrt(%d) = %.2f, ceil = %d)" 
                       col-count child-count (Math/sqrt child-count) (int (Math/ceil (Math/sqrt child-count)))))
      (println (format "3. Auto row count: %d (ceil(%d/%d) = %d)" 
                       row-count child-count col-count (int (Math/ceil (/ child-count col-count)))))
      
      ;; 2. Calculate individual child sizes
      (println "\n4. Individual child sizes:")
      (doseq [[idx child] (map-indexed vector children)]
        (let [size (c/layout-size child constraints)]
          (println (format "   Child %d: %s" idx size))))
      
      ;; 3. Calculate column and row sizes
      (println "\n5. Grid layout calculation:")
      (let [;; Organize children into grid positions
            grid-positions (for [child-idx (range child-count)]
                             {:child (nth children child-idx)
                              :col (mod child-idx col-count)
                              :row (int (Math/floor (/ child-idx col-count)))})
            
            ;; Group by column and row
            children-by-col (group-by :col grid-positions)
            children-by-row (group-by :row grid-positions)
            
            ;; Calculate column widths
            col-widths (doall (for [col-idx (range col-count)]
                                (let [children-in-col (get children-by-col col-idx [])
                                      widths (map #(nth (c/layout-size (:child %) constraints) 0) children-in-col)
                                      max-width (apply max 0 widths)]
                                  (println (format "   Column %d: children=%s, widths=%s, max=%d" 
                                                   col-idx (map :child children-in-col) widths max-width))
                                  max-width))
            
            ;; Calculate row heights  
            row-heights (doall (for [row-idx (range row-count)]
                                 (let [children-in-row (get children-by-row row-idx [])
                                       heights (map #(nth (c/layout-size (:child %) constraints) 1) children-in-row)
                                       max-height (apply max 0 heights)]
                                   (println (format "   Row %d: children=%s, heights=%s, max=%d" 
                                                    row-idx (map :child children-in-row) heights max-height))
                                   max-height))
        
        (println (format "   Column widths: %s" col-widths))
        (println (format "   Row heights: %s" row-heights))
        
        (let [total-width (apply + col-widths)
              total-height (apply + row-heights)]
          (println (format "   Total width: %d (sum of %s)" total-width col-widths))
          (println (format "   Total height: %d (sum of %s)" total-height row-heights))
          
          ;; 4. Expected vs actual
          (println "\n=== Expected vs Actual ===")
          (println (format "Expected: [110 30]"))
          (println (format "Calculated: [%d %d]" total-width total-height))
          (println (format "Width difference: %d" (- total-width 110)))
          (println (format "Height difference: %d" (- total-height 30)))
          
          [total-width total-height])))))

;; Alternative test with different configurations
(defn test-different-configs []
  (println "\n=== Testing Different Grid Configurations ===")
  
  (let [children test-children
        constraints [0 Long/MAX_VALUE 0 Long/MAX_VALUE]]
    
    ;; Test 1: Auto (current failing case)
    (let [grid (grid/grid :children children)]
      (println (format "Auto (no rows/cols): %s" (c/layout-size grid constraints))))
    
    ;; Test 2: Explicit 1 row, 2 columns
    (let [grid (grid/grid :cols 2 :rows 1 :children children)]
      (println (format "2 cols, 1 row: %s" (c/layout-size grid constraints))))
    
    ;; Test 3: Explicit 1 column, 2 rows
    (let [grid (grid/grid :cols 1 :rows 2 :children children)]
      (println (format "1 col, 2 rows: %s" (c/layout-size grid constraints))))
    
    ;; Test 4: Explicit 2x1 layout
    (let [grid (grid/grid :cols 2 :rows 1 :children children)]
      (println (format "2x1 grid: %s" (c/layout-size grid constraints))))))

(comment
  (debug-auto-sizing)
  (test-different-configs))