(ns examples.features.state-management
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Application state
(def app-state (atom {:volume 50
                      :brightness 75
                      :contrast 25}))

;; Components bound to different parts of the state
(def volume-control
  (rotary {:model (atom (:volume @app-state))
           :min 0
           :max 100
           :size 70
           :on-change (fn [new-value]
                        (swap! app-state assoc :volume new-value))}))

(def brightness-control
  (hslider {:model (atom (:brightness @app-state))
            :min 0
            :max 100
            :height 30
            :on-change (fn [new-value]
                         (swap! app-state assoc :brightness new-value))}))

(def contrast-control
  (hslider {:model (atom (:contrast @app-state))
            :min 0
            :max 100
            :height 30
            :on-change (fn [new-value]
                         (swap! app-state assoc :contrast new-value))}))

;; State display
(def state-display
  (p/text (str "State: " @app-state) {:text-size 14}))

;; Update display when state changes
(add-watch app-state :update-display
           (fn [_ _ _ new-state]
             (swap! state-display assoc :text (str "State: " new-state))))

(def app
  (p/stack [(p/text "State Management Example" {:text-size 20})
            (p/text "Managing application state with atoms")
            (p/cols [volume-control brightness-control contrast-control]
                    :gap 30)
            state-display
            (p/text "State changes are tracked and displayed")]
           :margin 20
           :gap 20))

(q/defsketch state-management-example
  :title "State Management Example"
  :size [600 350]
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