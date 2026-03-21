(ns examples.components.rotary.custom-drawing-rotary
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :as rotary]
   [casa.squid.plasticine.draw :as d]
   [quil.core :as q]))

;; Custom rotary draw function
(defn custom-rotary-draw [{:keys [min max value size background notch] 
                          :or {min 0 max 100}} x y w h]
  (let [center-x (+ x (/ w 2))
        center-y (+ y (/ h 2))
        radius (/ size 2)
        range-size (max (- max min) 1)
        ratio (/ (- value min) range-size)
        ;; Calculate angle based on value (0-360 degrees for full rotation)
        angle (+ (* ratio 270) 135) ; 135° to 405° (covers 270° range)
        notch-length (* radius 0.7)
        notch-x (+ center-x (* notch-length (q/cos (q/radians angle))))
        notch-y (+ center-y (* notch-length (q/sin (q/radians angle))))]
    
    ;; Draw background circle with gradient effect
    (d/with-props background
      (q/ellipse center-x center-y size size))
    
    ;; Draw decorative ring
    (d/with-props (assoc background :fill nil :stroke-weight 1)
      (q/ellipse center-x center-y (* size 0.8) (* size 0.8)))
    
    ;; Draw notch/indicator as a filled circle
    (d/with-props notch
      (q/ellipse notch-x notch-y 8 8))))

;; Custom rotary component with our draw function
(def custom-rotary-meta
  (assoc rotary/rotary-meta :-draw #'custom-rotary-draw))

(defn custom-rotary [flags]
  (atom (merge rotary/rotary-defaults flags)
        :meta custom-rotary-meta))

;; Create the custom rotary
(def custom-rotary-component
  (custom-rotary {:value 60
                  :min 0
                  :max 100
                  :size 90
                  :background {:fill [180 180 220]
                               :stroke [80 80 120]
                               :stroke-weight 2}
                  :notch {:fill [255 100 100]
                          :stroke 0
                          :stroke-weight 1}}))

(def app
  (p/stack [(p/text "Custom Drawing Rotary" {:text-size 20})
            custom-rotary-component
            (p/text "Rotary with custom drawing function")]
           :margin 20
           :gap 20))

(q/defsketch custom-rotary-example
  :title "Custom Drawing Rotary Example"
  :size [350 350]
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