(ns final-check
  (:require [casa.squid.plasticine.grid :as grid]
            [casa.squid.plasticine.component :as c]))

(defn test-component [width height]
  (atom {:width width :height height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

(let [children [(test-component 50 30) (test-component 60 40)]]
  (println "Test: 2 children [50x30 60x40]")
  
  ;; Test actual auto-sizing
  (let [grid (grid/grid :children children)]
    (println "Auto-sizing result:" (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
  
  ;; Test manual configurations
  (let [grid-2x1 (grid/grid :cols 2 :rows 1 :children children)]
    (println "2 cols, 1 row:" (c/layout-size grid-2x1 [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
  
  (let [grid-1x2 (grid/grid :cols 1 :rows 2 :children children)]
    (println "1 col, 2 rows:" (c/layout-size grid-1x2 [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
  
  ;; Manual calculation verification
  (println "\nManual calculation for 2x1:")
  (println "  Col 0 max width: 50 (child 0)")
  (println "  Col 1 max width: 60 (child 1)")
  (println "  Row 0 max height: 40 (max of 30, 40)")
  (println "  Total: [110 40]")
  
  (println "\nManual calculation for 1x2:")
  (println "  Col 0 max width: 60 (max of 50, 60)")
  (println "  Row 0 max height: 30 (child 0)")
  (println "  Row 1 max height: 40 (child 1)")
  (println "  Total: [60 70]")
  
  (println "\nISSUE: Expected [110 30] but actual auto gives [60 30]")
  (println "This suggests auto-sizing is using 1 column, 1 row instead of 2,1"))