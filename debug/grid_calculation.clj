;; WARNING: An LLM generated this namespace. Some things may not make sense,
;; contain numerous bugs, and generally be frustrating to work with. Tread carefully.

(ns debug.grid-calculation
  (:require [casa.squid.plasticine.grid :as grid]
            [casa.squid.plasticine.component :as c]))

;; Create simple test components with fixed sizes
(defn simple-component [width height]
  (let [size [width height]]
    (atom {:width width
           :height height
           :bounds [0 0 width height]}
          :meta {:-layout-size (fn [_ _] size)
                 :-min-intrinsic-width (fn [_ _] width)
                 :-max-intrinsic-width (fn [_ _] width)
                 :-min-intrinsic-height (fn [_ _] height)
                 :-max-intrinsic-height (fn [_ _] height)})))

;; Test components with specified sizes
(def test-components
  [(simple-component 50 30)   ; 50x30
   (simple-component 60 40)   ; 60x40
   (simple-component 70 35)   ; 70x35
   (simple-component 55 45)])  ; 55x45

;; Create grid with 2x2 layout
(def test-grid
  (grid/grid :cols 2 :rows 2 :children test-components))

;; Debug function to trace calculation
(defn debug-grid-calculation []
  (println "=== Grid Layout Debug ===")
  (println "Components:")
  (doseq [[idx comp] (map-indexed vector test-components)]
    (let [size (c/layout-size comp [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
      (println (format "  %d: %s (preferred: %s)" idx @comp size))))
  
  (println "\nGrid configuration:")
  (println (format "  cols: %s" (:cols @test-grid)))
  (println (format "  rows: %s" (:rows @test-grid)))
  
  (let [constraints [0 Long/MAX_VALUE 0 Long/MAX_VALUE]
        result (c/layout-size test-grid constraints)]
    (println (format "\nGrid layout-size with constraints %s:" constraints))
    (println (format "  Result: %s" result))
    (println (format "  Expected: [130 75]"))
    
    ;; Let's trace the calculation manually
    (println "\n=== Manual Calculation Trace ===")
    
    ;; Calculate column sizes
    (let [col-count 2
          row-count 2
          children test-components
          
          ;; Group children by column
          col-0-children [0 2] ; indices 0,2 in column 0
          col-1-children [1 3] ; indices 1,3 in column 1
          row-0-children [0 1] ; indices 0,1 in row 0  
          row-1-children [2 3] ; indices 2,3 in row 1
          
          ;; Calculate column widths (max width in each column)
          col-0-width (apply max (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0) col-0-children))
          col-1-width (apply max (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0) col-1-children))
          
          ;; Calculate row heights (max height in each row)
          row-0-height (apply max (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1) row-0-children))
          row-1-height (apply max (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1) row-1-children))]
      
      (println (format "Column 0 max width: %d (children: %s)" 
                       col-0-width 
                       (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0) col-0-children)))
      (println (format "Column 1 max width: %d (children: %s)" 
                       col-1-width 
                       (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 0) col-1-children)))
      (println (format "Row 0 max height: %d (children: %s)" 
                       row-0-height 
                       (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1) row-0-children)))
      (println (format "Row 1 max height: %d (children: %s)" 
                       row-1-height 
                       (map #(nth (c/layout-size (nth children %) [0 Long/MAX_VALUE 0 Long/MAX_VALUE]) 1) row-1-children)))
      
      (let [total-width (+ col-0-width col-1-width)
            total-height (+ row-0-height row-1-height)]
        (println (format "Total width: %d + %d = %d" col-0-width col-1-width total-width))
        (println (format "Total height: %d + %d = %d" row-0-height row-1-height total-height))))
    
    result))

;; Run the debug
(comment
  (debug-grid-calculation))