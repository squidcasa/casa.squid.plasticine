(ns examples.features.event-handling
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Event log display
(def event-log (atom []))

(def log-display
  (p/text "Event Log:\n" {:text-size 14}))

;; Function to add events to the log
(defn log-event [event]
  (swap! event-log (fn [log] (take 10 (conj log event)))) ; Keep only last 10 events
  (swap! log-display assoc :text (str "Event Log:\n" (clojure.string/join "\n" @event-log))))

;; Components with event handlers
(def rotary-with-events
  (rotary {:value 50
           :min 0
           :max 100
           :size 70
           :on-change (fn [new-value]
                        (log-event (str "Rotary changed to " (int new-value))) )}))

(def slider-with-events
  (hslider {:value 50
            :min 0
            :max 100
            :height 30
            :on-change (fn [new-value]
                         (log-event (str "Slider changed to " (int new-value))))}))

(def app
  (p/stack [(p/text "Event Handling Example" {:text-size 20})
            (p/cols [rotary-with-events slider-with-events]
                    :gap 30)
            log-display
            (p/text "Interact with controls to see events")]
           :margin 20
           :gap 20))

(q/defsketch event-handling-example
  :title "Event Handling Example"
  :size [500 400]
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