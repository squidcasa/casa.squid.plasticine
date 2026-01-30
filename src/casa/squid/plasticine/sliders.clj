(ns casa.squid.plasticine.sliders
  "Slider components"
  (:require
   [casa.squid.plasticine.draw :as d]
   [casa.squid.plasticine.object :as o]
   [quil.core :as q]))

(defn hslider-draw [{:keys [min max step value height bar bar-margin background text format]} x y w h]
  (d/with-props background
    (d/border-rect x y w h))
  (d/with-props bar
    (let [m (+ (:stroke-weight background 0) bar-margin)]
      (d/border-rect (+ x m)
                     (+ y m)
                     (/ (* (- w (* 2 m)) value) (- max min))
                     (- h (* 2 m)))))
  (d/with-props text
    (q/text (format value)
            (+ x (/ w 2))
            (+ y (/ h 2) (/ (d/prop :text-size) 3)))))

(defn vslider-draw [{:keys [min max step value width bar bar-margin background text format]} x y w h]
  (d/with-props background
    (d/border-rect x y w h))
  (d/with-props bar
    (let [m (+ (:stroke-weight background 0) bar-margin)
          bar-height (/ (* (- h (* 2 m)) value) (- max min))]
      (d/border-rect (+ x m)
                     (+ y (- h m) (- bar-height))
                     (- w (* 2 m))
                     bar-height)))
  (d/with-props text
    (q/push-matrix)
    (q/translate (+ x (/ w 2)) (+ y (/ h 2)))
    (q/rotate (- (/ Math/PI 2)))
    (q/text-align :center)
    (q/text (format value) 0 (/ (d/prop :text-size) 3))
    (q/pop-matrix)))

(defn hslider-size [c]
  [200 (:height c)])

(defn vslider-size [c]
  [(:width c) 200])

(defn hslider-mouse-pressed [c {:keys [x y]}]
  (swap! c
         (fn [{:keys [bounds min max] :as cv}]
           (let [[cx _ cw _] bounds]
             (assoc cv :value
                    (* (- max min)
                       (/ (- x cx) cw)))))))

(defn vslider-mouse-pressed [c {:keys [x y]}]
  (swap! c
         (fn [{:keys [bounds min max] :as cv}]
           (let [[_ cy _ ch] bounds]
             (assoc cv :value
                    (* (- max min)
                       (/ (- ch (- y cy)) ch)))))))

(defn hslider-cleanup [this]
  (o/unbind this [:value] (:model @this) []))

(defn vslider-cleanup [this]
  (when-let [model (:model @this)]
    (o/unbind this [:value] model [])))

(def hslider-meta
  {:-draw          #'hslider-draw
   :-pref-size     #'hslider-size
   :-mouse-pressed #'hslider-mouse-pressed
   :-mouse-dragged #'hslider-mouse-pressed
   :-cleanup       #'hslider-cleanup})

(def vslider-meta
  {:-draw          #'vslider-draw
   :-pref-size     #'vslider-size
   :-mouse-pressed #'vslider-mouse-pressed
   :-mouse-dragged #'vslider-mouse-pressed
   :-cleanup       #'vslider-cleanup})

(def hslider-defaults
  {:min        0
   :format     str
   :height     30
   :bar-margin 4
   :bar        {:fill [73 175 157]
                :stroke-weight 0}
   :background {:fill          150
                :stroke        0
                :stroke-weight 4}
   :text       {:text-align :center}})

(def vslider-defaults
  {:min        0
   :format     str
   :width      30
   :bar-margin 4
   :bar        {:fill [73 175 157]
                :stroke-weight 0}
   :background {:fill          150
                :stroke        0
                :stroke-weight 4}
   :text       {:text-align :center}})

(defn hslider
  "Horizontal slider
  - `:min` / `:max` Slider range. `:min` defaults to 0.
  - `:step` Optional step value, values are rounded to multiples of `:step`
  - `:value` / `:model` Initial value, or atom that should act as model
  - `:format` Function for what to show on the slider, receives current value, default [[str]]
  - `:on-change` Change callback, receives value
  - `:height` Height (width) of the slider
  - `:bar-margin` How much the bar is inset
  - `:bar` / `:background` / `:text` Style maps for the three parts of the sliders"
  [{:keys [min value model on-change] :as flags}]
  (let [value (or (when model @model) value min 0)
        slider (atom (assoc (merge hslider-defaults flags)
                            :value value
                            :min (or min 0))
                     :meta
                     hslider-meta)]
    (when model
      (o/bind<> slider [:value] model []))
    (when on-change
      (add-watch slider :on-change (fn [k r o n] (when (not= (:value o) (:value n)) (on-change (:value n))))))
    slider))

(defn vslider
  "Vertical slider
  - `:min` / `:max` Slider range. `:min` defaults to 0.
  - `:step` Optional step value, values are rounded to multiples of `:step`
  - `:value` / `:model` Initial value, or atom that should act as model
  - `:format` Function for what to show on the slider, receives current value, default [[str]]
  - `:on-change` Change callback, receives value
  - `:width` Width of the slider
  - `:bar-margin` How much the bar is inset
  - `:bar` / `:background` / `:text` Style maps for the three parts of the sliders"
  [{:keys [min value model on-change] :as flags}]
  (let [value (or (when model @model) value min 0)
        slider (atom (assoc (merge vslider-defaults flags)
                            :value value
                            :min (or min 0))
                     :meta
                     vslider-meta)]
    (when model
      (o/bind<> slider [:value] model []))
    (when on-change
      (add-watch slider :on-change (fn [k r o n] (when (not= (:value o) (:value n)) (on-change (:value n))))))
    slider))