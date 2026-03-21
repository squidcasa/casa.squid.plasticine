(ns examples.components.grid.auto-grid
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.text :refer [text]]
   [quil.core :as q]))

;; Create components with different preferred sizes
(def components
  [(rotary {:value 25 :size 50})
   (p/text "Small text")
   (rotary {:value 75 :size 80})
   (p/text "This is a longer text that takes more space")])

;; Create a grid with auto-sized cells
(def auto-grid
  (p/grid :cols [:auto :auto]  ; Auto-size columns based on content
          :rows [:auto :auto]  ; Auto-size rows based on content
          :children components))

(def app
  (p/stack [(p/text "Auto-sized Grid Cells" {:text-size 20})
            auto-grid
            (p/text "Cells sized according to their content")]
           :margin 20
           :gap 20))

(q/defsketch auto-grid-example
  :title "Auto-sized Grid Cells Example"
  :size [500 300]
  :features [:resizable :keep-on-top]
  :middleware [p/middleware]
  ::p/root #'app
  :settings #(q/smooth 2)
  ::p/defaults {:text-size 16
                :frame-rate 30
                :stroke 0
                :fill 0
                :stroke-weight 1
                :background [240 240 240]
                :rect-mode :corner})