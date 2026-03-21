;; WARNING: An LLM generated this namespace. Some things may not make sense,
;; contain numerous bugs, and generally be frustrating to work with. Tread
;; carefully.

(ns casa.squid.plasticine.rotary
  "Rotary knob component"
  (:require
   [casa.squid.plasticine.draw :as d]
   [casa.squid.plasticine.object :as o]
   [quil.core :as q]))

(defn rotary-draw [{:keys [min max value size background notch]
                    :or {min 0 max 100}} x y w h]
  (let [center-x (+ x (/ w 2))
        center-y (+ y (/ h 2))
        radius (/ size 2)
        range-size (clojure.core/max (- max min) 1)
        ratio (/ (- value min) range-size)
        ;; Calculate angle based on value (0-360 degrees for full rotation)
        angle (+ (* ratio 270) 135) ; 135° to 405° (covers 270° range)
        notch-length (* radius 0.7)
        notch-x (+ center-x (* notch-length (q/cos (q/radians angle))))
        notch-y (+ center-y (* notch-length (q/sin (q/radians angle))))]

    ;; Draw background circle
    (d/with-props background
      (q/ellipse center-x center-y size size))

    ;; Draw notch/indicator
    (d/with-props notch
      (q/line center-x center-y notch-x notch-y))))

(defn rotary-layout-size [{:keys [size]} [min-width max-width min-height max-height]]
  (let [size (or size 50)]
    [(min (max size min-width) max-width)
     (min (max size min-height) max-height)]))

(defn rotary-mouse-pressed [c {:keys [x y]}]
  (swap! c
         (fn [{:keys [min value bounds prev-x prev-y] max-size :max :as cv}]
           (if (and bounds (seq bounds))
             (let [[cx cy cw ch] bounds
                   ;; Calculate new value based on mouse position
                   ;; Moving up/right increases value, moving down/left decreases value
                   delta-x (- x (or prev-x x))
                   delta-y (- (or prev-y y) y)  ; Inverted Y (moving up is negative)
                   delta (+ delta-x delta-y)
                   range-size (- max-size min)
                   ;; Use the larger dimension for sensitivity
                   max-dim (clojure.core/max cw ch)
                   value-step (/ range-size (clojure.core/max max-dim 1))  ; Avoid division by zero
                   new-value (clojure.core/max min (clojure.core/min max-size (+ value (* delta value-step))))]
               (assoc cv
                      :value new-value
                      :prev-x x
                      :prev-y y))
             (assoc cv :prev-x x :prev-y y)))))

(defn rotary-mouse-dragged [c {:keys [x y]}]
  (swap! c
         (fn [{:keys [min value prev-x prev-y bounds] max-size :max :as cv}]
           (if (and bounds (seq bounds))
             (let [[cx cy cw ch] bounds
                   ;; Calculate new value based on mouse movement
                   ;; Moving up/right increases value, moving down/left decreases value
                   delta-x (- x (or prev-x x))
                   delta-y (- (or prev-y y) y)  ; Inverted Y (moving up is negative)
                   delta (+ delta-x delta-y)
                   range-size (- max-size min)
                   ;; Use the larger dimension for sensitivity
                   max-dim (clojure.core/max cw ch)
                   value-step (/ range-size (clojure.core/max max-dim 1))  ; Avoid division by zero
                   new-value (clojure.core/max min (clojure.core/min max-size (+ value (* delta value-step))))]
               (assoc cv
                      :value new-value
                      :prev-x x
                      :prev-y y))
             (assoc cv :prev-x x :prev-y y)))))

(defn rotary-cleanup [this]
  (when-let [model (:model @this)]
    (o/unbind this [:value] model [])))

(def rotary-meta
  {:-draw          #'rotary-draw
   :-layout-size   #'rotary-layout-size
   :-mouse-pressed #'rotary-mouse-pressed
   :-mouse-dragged #'rotary-mouse-dragged
   :-cleanup       #'rotary-cleanup})

(def rotary-defaults
  {:min 0
   :max 100
   :size 50
   :background {:fill [200 200 200]
                :stroke 0
                :stroke-weight 2}
   :notch {:stroke 0
           :stroke-weight 3}})

(defn rotary
  "Rotary knob component
  - `:min` / `:max` Knob range. Defaults to 0-100.
  - `:value` / `:model` Initial value, or atom that should act as model
  - `:size` Diameter of the knob in pixels
  - `:on-change` Change callback, receives value
  - `:background` Style map for the knob background
  - `:notch` Style map for the indicator notch"
  [{:keys [min max value model on-change] :as flags}]
  (let [value (or (when model @model) value 0)
        min (or min 0)
        max (or max 100)
        rotary (atom (assoc (merge rotary-defaults flags)
                            :value value
                            :min min
                            :max max)
                     :meta
                     rotary-meta)]
    (when model
      (o/bind<> rotary [:value] model []))
    (when on-change
      (add-watch rotary :on-change (fn [k r o n] (when (not= (:value o) (:value n)) (on-change (:value n))))))
    rotary))

(comment
  (rotary {:value 50 :size 60}))
