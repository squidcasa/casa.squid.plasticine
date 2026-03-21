(ns simple-debug
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
  (println "Test case: 2 children [50x30 60x40]")
  (println "Expected: [110 30]")
  (let [result (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
    (println "Actual:" result)
    (println "Match?" (= result [110 30]))))