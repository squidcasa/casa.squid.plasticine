(ns examples.demos.music-synth
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider vslider]]
   [quil.core :as q]))

;; Synthesizer state
(def synth-state (atom {:oscillator :sine
                        :frequency 440
                        :amplitude 0.5
                        :filter-cutoff 1000
                        :filter-resonance 0.7
                        :lfo-rate 5
                        :lfo-depth 0.1}))

;; Oscillator types
(def oscillator-types [:sine :square :sawtooth :triangle])

;; Function to create a synth control section
(defn synth-control-section [title & controls]
  (p/stack (concat [(p/text title {:text-size 16 :fill 50})]
                   controls)
           :gap 10))

;; Create synth controls
(def oscillator-control
  (synth-control-section "Oscillator"
                         (p/text (str "Type: " (name (:oscillator @synth-state))) {:text-size 14})
                         (hslider {:model (atom (:frequency @synth-state))
                                   :min 55
                                   :max 1760
                                   :height 25
                                   :format #(str "Freq: " (int %) " Hz")
                                   :on-change (fn [new-value]
                                                (swap! synth-state assoc :frequency new-value))})))

(def amplitude-control
  (synth-control-section "Amplitude"
                         (vslider {:model (atom (:amplitude @synth-state))
                                   :min 0.0
                                   :max 1.0
                                   :width 30
                                   :format #(str "Vol: " (format "%.2f" %))
                                   :on-change (fn [new-value]
                                                (swap! synth-state assoc :amplitude new-value))})))

(def filter-control
  (synth-control-section "Filter"
                         (hslider {:model (atom (:filter-cutoff @synth-state))
                                   :min 100
                                   :max 5000
                                   :height 25
                                   :format #(str "Cutoff: " (int %) " Hz")
                                   :on-change (fn [new-value]
                                                (swap! synth-state assoc :filter-cutoff new-value))})
                         (rotary {:model (atom (:filter-resonance @synth-state))
                                  :min 0.0
                                  :max 1.0
                                  :size 50
                                  :on-change (fn [new-value]
                                               (swap! synth-state assoc :filter-resonance new-value))})))

(def lfo-control
  (synth-control-section "LFO"
                         (hslider {:model (atom (:lfo-rate @synth-state))
                                   :min 0.1
                                   :max 20
                                   :height 25
                                   :format #(str "Rate: " (format "%.1f" %) " Hz")
                                   :on-change (fn [new-value]
                                                (swap! synth-state assoc :lfo-rate new-value))})
                         (rotary {:model (atom (:lfo-depth @synth-state))
                                  :min 0.0
                                  :max 1.0
                                  :size 50
                                  :on-change (fn [new-value]
                                               (swap! synth-state assoc :lfo-depth new-value))})))

;; State display
(def state-display
  (p/text (str "Synth: " @synth-state) {:text-size 10}))

;; Update display when state changes
(add-watch synth-state :update-display
           (fn [_ _ _ new-state]
             (swap! state-display assoc :text (str "Synth: " new-state))))

(def app
  (p/stack [(p/text "Music Synthesizer Interface" {:text-size 20})
            (p/text "Simple synthesizer UI with knobs, sliders, and buttons")
            (p/cols [oscillator-control
                     amplitude-control
                     filter-control
                     lfo-control]
                    :gap 20)
            state-display]
           :margin 20
           :gap 20))

(q/defsketch music-synth-demo
  :title "Music Synthesizer Interface Demo"
  :size [800 500]
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