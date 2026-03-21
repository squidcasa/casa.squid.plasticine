(ns debug-flex
  (:require [casa.squid.plasticine.container :as container]
            [casa.squid.plasticine.component :as c]))

(defn test-component [width height]
  (atom {:width width :height height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

(defn flex-component [width height flex-width flex-height]
  (atom {:width width :height height :flex-width flex-width :flex-height flex-height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

(let [fixed (test-component 50 30)
      flex1 (flex-component 0 30 1 0)
      flex2 (flex-component 0 40 2 0)
      cols (container/cols [fixed flex1 flex2])]
  (println "Fixed size:" (c/layout-size fixed [0 1000000 0 1000000]))
  (println "Flex1 size:" (c/layout-size flex1 [0 1000000 0 1000000]))
  (println "Flex2 size:" (c/layout-size flex2 [0 1000000 0 1000000]))
  (println "Cols size:" (c/layout-size cols [0 1000000 0 1000000]))
  (println "Expected:" [150 40]))