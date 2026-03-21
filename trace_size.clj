(ns trace-grid-size
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
  
  (println "Testing grid-layout-size directly:")
  (let [result (grid/grid-layout-size @grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])]
    (println "grid-layout-size result:" result))
  
  (println "\nTesting grid-pref-size directly:")
  (let [result (grid/grid-pref-size @grid)]
    (println "grid-pref-size result:" result))
  
  (println "\nTesting grid-min-size directly:")
  (let [result (grid/grid-min-size @grid)]
    (println "grid-min-size result:" result)))