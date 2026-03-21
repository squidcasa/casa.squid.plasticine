(ns simple-step-trace
  (:require [casa.squid.plasticine.grid :as grid]
            [casa.squid.plasticine.component :as c]))

(defn test-component [width height]
  (atom {:width width :height height}
        :meta {:-layout-size (fn [_ _] [width height])}))

(let [children [(test-component 50 30) (test-component 60 40)]
      grid (grid/grid :children children)]
  
  (println "Children sizes:" (map #(c/layout-size % [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) children))
  (println "Grid config:" (select-keys @grid [:cols :rows :children]))
  
  ;; Manual calculation
  (let [child-count 2
        col-count (max 1 (int (Math/ceil (Math/sqrt child-count))))
        row-count (max 1 (int (Math/ceil (/ child-count col-count))))]
    
    (println "Auto calculation:")
    (println "  child-count:" child-count)
    (println "  col-count:" col-count)
    (println "  row-count:" row-count)
    
    (let [col-sizes [50 60]
          row-sizes [40]]
      
      (println "Expected calculation:")
      (println "  col-sizes:" col-sizes "→" (apply + col-sizes))
      (println "  row-sizes:" row-sizes "→" (apply + row-sizes))
      
      (println "Actual result:" (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))))