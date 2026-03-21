(ns casa.squid.plasticine.layout-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [casa.squid.plasticine.component :as c]
   [casa.squid.plasticine.container :as container]
   [casa.squid.plasticine.grid :as grid]
   [casa.squid.plasticine.text :as text]
   [casa.squid.plasticine.sliders :as sliders]
   [casa.squid.plasticine.rotary :as rotary]))

;; Test helper functions
(defn- test-component
  "Create a simple test component with fixed size"
  [width height]
  (atom {:width width :height height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

(defn- flex-component
  "Create a test component with flex attributes"
  [width height flex-width flex-height]
  (atom {:width width :height height :flex-width flex-width :flex-height flex-height}
        :meta {:-layout-size (fn [_ constraints]
                               (let [[min-w max-w min-h max-h] constraints]
                                 [(min (max width min-w) max-w)
                                  (min (max height min-h) max-h)]))}))

(deftest test-basic-layout
  (testing "Basic component layout-size functionality"
    (let [comp (test-component 100 50)]
      (is (= [100 50] (c/layout-size comp [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [50 25] (c/layout-size comp [0 50 0 25])))
      (is (= [200 75] (c/layout-size comp [200 300 75 100])))
      (is (= [100 50] (c/layout-size comp [50 200 25 100]))))))

(deftest test-constraint-handling
  (testing "Respects minimum constraints"
    (let [comp (test-component 50 30)]
      (is (= [100 50] (c/layout-size comp [100 200 50 100])))
      (is (= [50 30] (c/layout-size comp [0 100 30 100])))))

  (testing "Respects maximum constraints"
    (let [comp (test-component 100 60)]
      (is (= [80 30] (c/layout-size comp [0 80 0 30])))
      (is (= [100 60] (c/layout-size comp [0 150 0 100])))))

  (testing "Handles edge cases"
    (let [comp (test-component 100 50)]
      (is (= [0 0] (c/layout-size comp [0 0 0 0])))
      (is (= [100 50] (c/layout-size comp [0 Long/MAX_VALUE 0 Long/MAX_VALUE]))))))

(deftest test-container-layout
  (testing "Cols container with fixed children"
    (let [child1 (test-component 50 30)
          child2 (test-component 60 40)
          cols (container/cols [child1 child2])]
      (is (= [110 40] (c/layout-size cols [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [100 40] (c/layout-size cols [0 100 0 Long/MAX_VALUE])))))

  (testing "Stack container with fixed children"
    (let [child1 (test-component 50 30)
          child2 (test-component 60 40)
          stack (container/stack [child1 child2])]
      (is (= [60 70] (c/layout-size stack [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [60 50] (c/layout-size stack [0 Long/MAX_VALUE 0 50])))))

  (testing "Cols container with gap"
    (let [child1 (test-component 50 30)
          child2 (test-component 60 40)
          cols (container/cols [child1 child2] :gap 10)]
      (is (= [120 40] (c/layout-size cols [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))))

  (testing "Stack container with gap"
    (let [child1 (test-component 50 30)
          child2 (test-component 60 40)
          stack (container/stack [child1 child2] :gap 5)]
      (is (= [60 75] (c/layout-size stack [0 Long/MAX_VALUE 0 Long/MAX_VALUE]))))))

(deftest test-grid-layout
  (testing "Grid container with fixed children"
    (let [children [(test-component 50 30) (test-component 60 40)
                    (test-component 70 35) (test-component 55 45)]
          grid (grid/grid :cols 2 :rows 2 :children children)]
      (is (= [130 85] (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [100 80] (c/layout-size grid [0 100 0 80])))))

  (testing "Grid container with single column"
    (let [children [(test-component 50 30) (test-component 60 40)]
          grid (grid/grid :cols 1 :rows 2 :children children)]
      (is (= [60 70] (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))))

  (testing "Grid container with single row"
    (let [children [(test-component 50 30) (test-component 60 40)]
          grid (grid/grid :cols 2 :rows 1 :children children)]
      (is (= [110 40] (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))))

  (testing "Grid container with auto sizing"
    (let [children [(test-component 50 30) (test-component 60 40)]
          grid (grid/grid :children children)]
      (is (= [110 40] (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE]))))))

(deftest test-flex-layout
  (testing "Cols container distributes flex space"
    (let [child1 (flex-component 50 30 1 0)
          child2 (flex-component 60 40 2 0)
          cols (container/cols [child1 child2])]
      (is (= [110 40] (c/layout-size cols [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [90 40] (c/layout-size cols [0 90 0 Long/MAX_VALUE])))))

  (testing "Stack container distributes flex space"
    (let [child1 (flex-component 50 30 0 1)
          child2 (flex-component 60 40 0 2)
          stack (container/stack [child1 child2])]
      (is (= [60 70] (c/layout-size stack [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [60 60] (c/layout-size stack [0 Long/MAX_VALUE 0 60])))))

  (testing "Mixed fixed and flex children"
    (let [fixed (test-component 50 30)
          flex1 (flex-component 0 30 1 0)
          flex2 (flex-component 0 40 2 0)
          cols (container/cols [fixed flex1 flex2])]
      (is (= [150 40] (c/layout-size cols [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [120 40] (c/layout-size cols [0 120 0 Long/MAX_VALUE]))))))

(deftest test-flex-attributes
  (testing "Flex attribute access"
    (let [comp1 (atom {:flex-width 2 :flex-height 3})
          comp2 (atom {:flex 3})
          comp3 (atom {})]
      (is (= 2 (c/flex-width comp1)))
      (is (= 3 (c/flex-width comp2)))
      (is (= 0 (c/flex-width comp3)))
      (is (= 3 (c/flex-height comp1)))
      (is (= 3 (c/flex-height comp2)))
      (is (= 0 (c/flex-height comp3)))))

  (testing "Flex attribute precedence"
    (let [comp (atom {:flex 5 :flex-width 2 :flex-height 3})]
      (is (= 2 (c/flex-width comp)))
      (is (= 3 (c/flex-height comp)))
      (is (= 5 (c/flex-width (assoc @comp :flex-width nil))))
      (is (= 5 (c/flex-height (assoc @comp :flex-height nil)))))))

(deftest test-real-components
  (testing "Text component sizing"
    (let [txt (text/text "Hello World" {})]
      (is (> (first (c/layout-size txt [0 1000 0 1000])) 50))
      (is (> (second (c/layout-size txt [0 1000 0 1000])) 10))))

  (testing "Slider components"
    (let [hslider (sliders/hslider {:min 0 :max 100 :value 50})]
      (is (= [100 20] (c/layout-size hslider [0 100 0 20])))
      (is (= [150 20] (c/layout-size hslider [150 150 20 20]))))

    (let [vslider (sliders/vslider {:min 0 :max 100 :value 50})]
      (is (= [20 100] (c/layout-size vslider [0 20 0 100])))
      (is (= [20 200] (c/layout-size vslider [20 20 200 200])))))

  (testing "Rotary component"
    (let [rotary (rotary/rotary {:size 60})]
      (is (= [60 60] (c/layout-size rotary [0 100 0 100])))
      (is (= [40 40] (c/layout-size rotary [0 40 0 40]))))))

(deftest test-empty-containers
  (testing "Empty containers return minimum size"
    (is (= [0 0] (c/layout-size (container/cols []) [0 100 0 100])))
    (is (= [0 0] (c/layout-size (container/stack []) [0 100 0 100])))
    (is (= [0 0] (c/layout-size (grid/grid :cols 2 :rows 2 :children []) [0 100 0 100])))))

(deftest test-single-child
  (testing "Single child containers"
    (let [child (test-component 50 30)]
      (is (= [50 30] (c/layout-size (container/cols [child]) [0 100 0 100])))
      (is (= [50 30] (c/layout-size (container/stack [child]) [0 100 0 100]))))))

(deftest test-constraint-dependency
  (testing "Layout respects width-height dependencies"
    (let [comp (atom {}
                     :meta {:-layout-size (fn [_ constraints]
                                            (let [[min-w max-w min-h max-h] constraints]
                                              ;; Example: text that grows taller when width is constrained
                                              (let [width (min (max 100 min-w) max-w)
                                                    height (max 20 (* 2 (max 0 (- 100 width))))]
                                                [width (min (max height min-h) max-h)])))})]
      (is (= [100 20] (c/layout-size comp [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))
      (is (= [50 100] (c/layout-size comp [0 50 0 Long/MAX_VALUE]))))))

(deftest test-bound-constraints
  (testing "Bounds are respected"
    (let [comp (test-component 200 100)]
      (let [bounds [0 0 150 80]
            cols (container/cols [comp] :bounds bounds)]
        (is (= [150 80] (c/layout-size cols [0 Long/MAX_VALUE 0 Long/MAX_VALUE])))))))

(deftest test-complex-layouts
  (testing "Nested containers"
    (let [child1 (test-component 50 30)
          child2 (test-component 60 40)
          inner-cols (container/cols [child1 child2])
          inner-stack (container/stack [(test-component 80 20) child2])
          outer-grid (grid/grid :cols 1 :rows 2 :children [inner-cols inner-stack])]
      (is (= [190 90] (c/layout-size outer-grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE]))))))

(deftest test-performance
  (testing "Layout calculations are efficient"
    (let [children (repeatedly 10 #(test-component 50 30))
          grid (grid/grid :cols 5 :rows 2 :children children)]
      (is (= [250 60] (c/layout-size grid [0 Long/MAX_VALUE 0 Long/MAX_VALUE]))))))

(deftest test-edge-cases
  (testing "Zero constraints"
    (let [comp (test-component 100 50)]
      (is (= [0 0] (c/layout-size comp [0 0 0 0])))))

  (testing "Negative constraints"
    (let [comp (test-component 100 50)]
      (is (= [100 50] (c/layout-size comp [-10 100 -5 50])))))

  (testing "Very large constraints"
    (let [comp (test-component 100 50)]
      (is (= [100 50] (c/layout-size comp [0 1e9 0 1e9])))))

  (testing "Invalid constraints (min > max)"
    (let [comp (test-component 100 50)]
      (is (= [100 50] (c/layout-size comp [200 100 300 50]))))))
