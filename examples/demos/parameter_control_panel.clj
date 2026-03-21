(ns examples.demos.parameter-control-panel
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Parameter state
(def parameter-state (atom {:frequency 440
                            :resonance 0.7
                            :attack 0.1
                            :decay 0.3
                            :sustain 0.8
                            :release 0.5}))

;; Function to create a parameter control
(defn parameter-control [label param-key min-val max-val step]
  (let [control (hslider {:model (atom (get @parameter-state param-key))
                          :min min-val
                          :max max-val
                          :height 25
                          :format #(str (int (* 100 %)) " ")
                          :on-change (fn [new-value]
                                       (swap! parameter-state assoc param-key new-value))})]
    (p/stack [(p/text label {:text-size 14})
              control
              (p/text (str min-val "    " max-val) {:text-size 10})]
             :gap 2)))

;; Create parameter controls
(def frequency-control (parameter-control "Frequency" :frequency 20 20000 1))
(def resonance-control (parameter-control "Resonance" :resonance 0.0 1.0 0.01))
(def attack-control (parameter-control "Attack" :attack 0.0 5.0 0.01))
(def decay-control (parameter-control "Decay" :decay 0.0 5.0 0.01))
(def sustain-control (parameter-control "Sustain" :sustain 0.0 1.0 0.01))
(def release-control (parameter-control "Release" :release 0.0 5.0 0.01))

;; State display
(def state-display
  (p/text (str "Parameters: " @parameter-state) {:text-size 12}))

;; Update display when state changes
(add-watch parameter-state :update-display
           (fn [_ _ _ new-state]
             (swap! state-display assoc :text (str "Parameters: " new-state))))

(def app
  (p/stack [(p/text "Parameter Control Panel" {:text-size 20})
            (p/text "Dashboard with various controls for adjusting parameters")
            frequency-control
            resonance-control
            attack-control
            decay-control
            sustain-control
            release-control
            state-display]
           :margin 20
           :gap 15))

(q/defsketch parameter-control-panel-demo
  :title "Parameter Control Panel Demo"
  :size [500 600]
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