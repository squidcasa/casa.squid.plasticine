;; WARNING: An LLM generated this namespace. Some things may not make sense,
;; contain numerous bugs, and generally be frustrating to work with. Tread carefully.

(ns debug.grid-calculation-detailed
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

;; Alternative arrangement check - let's verify the exact grid positions
(defn verify-grid-positions []
  (println "=== Verifying Grid Positions ===")
  (println "Grid layout (2x2):")
  (println "  [0,0] [0,1]")
  (println "  [1,0] [1,1]")
  (println "")
  
  (println "Component mapping:")
  (doseq [[idx comp] (map-indexed vector test-components)]
    (let [col (mod idx 2)
          row (int (/ idx 2))
          size (c/layout-size comp [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
      (println (format "  Component %d at [%d,%d]: %s" idx row col size))))
  
  (println "\nColumn analysis:")
  (let [col-0-sizes [(c/layout-size (nth test-components 0) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])
                     (c/layout-size (nth test-components 2) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
        col-1-sizes [(c/layout-size (nth test-components 1) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])
                     (c/layout-size (nth test-components 3) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]]
    (println (format "  Column 0: %s -> max width = %d" 
                     col-0-sizes 
                     (apply max (map first col-0-sizes))))
    (println (format "  Column 1: %s -> max width = %d" 
                     col-1-sizes 
                     (apply max (map first col-1-sizes)))))
  
  (println "\nRow analysis:")
  (let [row-0-sizes [(c/layout-size (nth test-components 0) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])
                     (c/layout-size (nth test-components 1) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
        row-1-sizes [(c/layout-size (nth test-components 2) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])
                     (c/layout-size (nth test-components 3) [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]]
    (println (format "  Row 0: %s -> max height = %d" 
                     row-0-sizes 
                     (apply max (map second row-0-sizes))))
    (println (format "  Row 1: %s -> max height = %d" 
                     row-1-sizes 
                     (apply max (map second row-1-sizes)))))
  
  (println "\nLet's re-calculate based on the exact arrangement:")
  (let [;; Column 0: components 0 and 2
        col-0-width (max 50 70)  ; 70
        
        ;; Column 1: components 1 and 3  
        col-1-width (max 60 55)  ; 60
        
        ;; Row 0: components 0 and 1
        row-0-height (max 30 40) ; 40
        
        ;; Row 1: components 2 and 3
        row-1-height (max 35 45) ; 45
        
        total-width (+ col-0-width col-1-width)
        total-height (+ row-0-height row-1-height)]
    
    (println (format "  Column widths: %d + %d = %d" col-0-width col-1-width total-width))
    (println (format "  Row heights: %d + %d = %d" row-0-height row-1-height total-height))
    (println (format "  Final size: [%d %d]" total-width total-height))
    
    (println "\nPossible interpretations:")
    (println "  1. Expected [130 75] - this suggests different component heights")
    (println "  2. Current [130 85] - this matches max(30,40)=40 and max(35,45)=45")
    (println "  3. Check if test data is correct or if there's a bug in expected value")))

(comment
  (verify-grid-positions))