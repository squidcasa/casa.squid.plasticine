(ns examples.demos.interactive-visualization
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Visualization parameters
(def viz-state (atom {:rotation 0
                      :scale 1.0
                      :color-hue 0
                      :segments 8}))

;; Custom draw function for visualization
(defn draw-visualization [params]
  (let [{:keys [rotation scale color-hue segments]} params
        center-x (/ (q/width) 2)
        center-y (/ (q/height) 2)
        radius (* 100 scale)]
    (q/push-matrix)
    (q/translate center-x center-y)
    (q/rotate (q/radians rotation))
    (q/color-mode :hsb 100)
    (doseq [i (range segments)]
      (let [angle (* (/ 360 segments) i)
            x (* radius (q/cos (q/radians angle)))
            y (* radius (q/sin (q/radians angle)))
            hue (mod (+ color-hue (* 10 i)) 100)]
        (q/stroke hue 80 90)
        (q/stroke-weight 3)
        (q/line 0 0 x y)))
    (q/pop-matrix)
    (q/color-mode :rgb 255)))

;; Create control components
(def rotation-control
  (rotary {:model (atom (:rotation @viz-state))
           :min 0
           :max 360
           :size 70
           :on-change (fn [new-value]
                        (swap! viz-state assoc :rotation new-value))}))

(def scale-control
  (hslider {:model (atom (:scale @viz-state))
            :min 0.5
            :max 2.0
            :height 25
            :on-change (fn [new-value]
                         (swap! viz-state assoc :scale new-value))}))

(def color-control
  (hslider {:model (atom (:color-hue @viz-state))
            :min 0
            :max 100
            :height 25
            :on-change (fn [new-value]
                         (swap! viz-state assoc :color-hue new-value))}))

(def segments-control
  (hslider {:model (atom (:segments @viz-state))
            :min 3
            :max 20
            :height 25
            :format #(str (int %) " segments")
            :on-change (fn [new-value]
                         (swap! viz-state assoc :segments (int new-value)))}))

;; Visualization component
(def visualization
  (atom {:draw-fn (fn []
                    (draw-visualization @viz-state))}))

;; Add draw method to visualization component
(alter-meta! visualization assoc :-draw
             (fn [this x y w h]
               (q/push-matrix)
               (q/translate x y)
               ((:draw-fn @this))
               (q/pop-matrix)))

(def app
  (p/stack [(p/text "Interactive Visualization" {:text-size 20})
            (p/text "UI controls that affect a visual display")
            visualization
            (p/text "Rotation:")
            rotation-control
            (p/text "Scale:")
            scale-control
            (p/text "Color:")
            color-control
            (p/text "Segments:")
            segments-control]
           :margin 20
           :gap 15))

(q/defsketch interactive-visualization-demo
  :title "Interactive Visualization Demo"
  :size [500 700]
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