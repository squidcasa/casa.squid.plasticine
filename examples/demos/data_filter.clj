(ns examples.demos.data-filter
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Sample data to filter
(def sample-data
  [{:name "Item 1" :value 25 :category :a}
   {:name "Item 2" :value 75 :category :b}
   {:name "Item 3" :value 40 :category :a}
   {:name "Item 4" :value 90 :category :c}
   {:name "Item 5" :value 15 :category :b}
   {:name "Item 6" :value 60 :category :c}
   {:name "Item 7" :value 35 :category :a}
   {:name "Item 8" :value 80 :category :b}])

;; Filter state
(def filter-state (atom {:min-value 0
                         :max-value 100
                         :category :all}))

;; Categories
(def categories [:all :a :b :c])

;; Function to filter data based on state
(defn filter-data [data filter-state]
  (let [{:keys [min-value max-value category]} filter-state]
    (filter (fn [item]
              (and (>= (:value item) min-value)
                   (<= (:value item) max-value)
                   (or (= category :all) (= (:category item) category))))
            data)))

;; Create filter controls
(def min-value-control
  (p/cols [(p/text "Min Value:" {:text-size 14})
           (hslider {:model (atom (:min-value @filter-state))
                     :min 0
                     :max 100
                     :height 25
                     :format #(str (int %))
                     :on-change (fn [new-value]
                                  (swap! filter-state assoc :min-value new-value))})]
          :gap 10))

(def max-value-control
  (p/cols [(p/text "Max Value:" {:text-size 14})
           (hslider {:model (atom (:max-value @filter-state))
                     :min 0
                     :max 100
                     :height 25
                     :format #(str (int %))
                     :on-change (fn [new-value]
                                  (swap! filter-state assoc :max-value new-value))})]
          :gap 10))

(def category-control
  (p/cols [(p/text "Category:" {:text-size 14})
           (p/text (name (:category @filter-state)) {:text-size 14})]
          :gap 10))

;; Filtered data display
(def filtered-data-display
  (atom {:text "Filtered data will appear here"}
        :meta {:-draw (fn [this x y w h]
                        (let [filtered (filter-data sample-data @filter-state)
                              display-text (if (empty? filtered)
                                             "No items match the filter"
                                             (clojure.string/join "\n" (map :name filtered)))]
                          (q/text-align :left)
                          (q/fill 0)
                          (q/text display-text x (+ y 15))))}))

;; Update display when state changes
(add-watch filter-state :update-display
           (fn [_ _ _ new-state]
             (let [filtered (filter-data sample-data new-state)
                   display-text (if (empty? filtered)
                                  "No items match the filter"
                                  (clojure.string/join "\n" (map :name filtered)))]
               (swap! filtered-data-display assoc :text display-text))))

(def app
  (p/stack [(p/text "Data Filter Controls" {:text-size 20})
            (p/text "Interface for filtering and manipulating data with multiple controls")
            min-value-control
            max-value-control
            category-control
            (p/text "Filtered Data:" {:text-size 16})
            filtered-data-display]
           :margin 20
           :gap 20))

(q/defsketch data-filter-demo
  :title "Data Filter Controls Demo"
  :size [400 400]
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

(comment
  (meta min-value-control)
  (p/dispatch min-value-control :-draw 0 0 200 200)

  (p/pref-size
   (nth (:children @(nth (:children @app) 2)) 1))
  )
