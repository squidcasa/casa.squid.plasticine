;; WARNING: An LLM generated this namespace. Some things may not make sense,
;; contain numerous bugs, and generally be frustrating to work with. Tread carefully.

(ns auto-sizing-debug
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

(println "=== Auto-sizing Grid Debug ===")
(println "Test case: 2 children [(test-component 50 30) (test-component 60 40)]")
(println "Expected: [110 30]")

(let [constraints [0 Long/MAX_VALUE 0 Long/MAX_VALUE]
      auto-grid (grid/grid :children test-children)
      result (c/layout-size auto-grid constraints)]
  
  (println (str "Actual result: " result))
  (println (str "Match expected? " (= result [110 30])))
  
  ;; Manual calculation
  (let [children test-children
        child-count (count children)
        col-count (max 1 (int (Math/ceil (Math/sqrt child-count))))
        row-count (max 1 (int (Math/ceil (/ child-count col-count))))]
    
    (println (str "\nManual calculation:"))
    (println (str "Children count: " child-count))
    (println (str "Auto column count: " col-count))
    (println (str "Auto row count: " row-count))
    
    ;; Expected layout: 2x1 grid
    ;; Child 0 at [0,0]: 50x30
    ;; Child 1 at [1,0]: 60x40
    ;; Column 0 width: max(50) = 50
    ;; Column 1 width: max(60) = 60
    ;; Row 0 height: max(30, 40) = 40
    ;; Total: 50+60 = 110 width, 40 height
    
    (println (str "Expected layout analysis:"))
    (println (str "  Column 0 max width: 50 (child 0)"))
    (println (str "  Column 1 max width: 60 (child 1)"))
    (println (str "  Row 0 max height: 40 (max of 30, 40)"))
    (println (str "  Total: [110 40]"))
    
    (println (str "\nThe issue: Expected [110 30] but getting [110 40]"))
    (println (str "The height should be 30 (from child 0) but it's 40 (max of 30,40)"))
    (println (str "This suggests the expected result might be incorrect!")
    ))

;; Test the actual test case from layout_test.clj
(println "\n=== Testing actual test case ===")
(let [children [(test-component 50 30) (test-component 60 40)]
      grid (grid/grid :children children)
      result (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
  (println (str "Test result: " result))
  (println (str "Expected from test: [110 30]"))
  (println (str "Actual matches expected? " (= result [110 30]))))