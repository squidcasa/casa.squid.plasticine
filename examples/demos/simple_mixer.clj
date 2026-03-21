(ns examples.demos.simple-mixer
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [quil.core :as q]))

;; Mixer channels state
(def mixer-state (atom {:channel1 {:volume 75 :pan 50}
                        :channel2 {:volume 60 :pan 50}
                        :channel3 {:volume 80 :pan 50}
                        :master {:volume 70}}))

;; Function to create a mixer channel
(defn mixer-channel [channel-id label]
  (let [volume-control (rotary {:model (atom (get-in @mixer-state [channel-id :volume]))
                                :min 0
                                :max 100
                                :size 60
                                :on-change (fn [new-value]
                                             (swap! mixer-state assoc-in [channel-id :volume] new-value))})
        pan-control (hslider {:model (atom (get-in @mixer-state [channel-id :pan]))
                              :min 0
                              :max 100
                              :height 20
                              :on-change (fn [new-value]
                                           (swap! mixer-state assoc-in [channel-id :pan] new-value))})]
    (p/stack [(p/text label {:text-size 16})
              (p/text "Vol" {:text-size 12})
              volume-control
              (p/text "Pan" {:text-size 12})
              pan-control]
             :gap 5)))

;; Create mixer channels
(def channel1 (mixer-channel :channel1 "Channel 1"))
(def channel2 (mixer-channel :channel2 "Channel 2"))
(def channel3 (mixer-channel :channel3 "Channel 3"))

;; Master control
(def master-control
  (let [master-volume (rotary {:model (atom (get-in @mixer-state [:master :volume]))
                               :min 0
                               :max 100
                               :size 70
                               :on-change (fn [new-value]
                                            (swap! mixer-state assoc-in [:master :volume] new-value))})]
    (p/stack [(p/text "Master" {:text-size 16})
              master-volume]
             :gap 5)))

;; State display
(def state-display
  (p/text (str "Mixer State: " @mixer-state) {:text-size 12}))

;; Update display when state changes
(add-watch mixer-state :update-display
           (fn [_ _ _ new-state]
             (swap! state-display assoc :text (str "Mixer State: " new-state))))

(def app
  (p/stack [(p/text "Simple Mixer Demo" {:text-size 20})
            (p/cols [channel1 channel2 channel3 master-control]
                    :gap 30)
            state-display
            (p/text "Audio mixer-style interface with volume and pan controls")]
           :margin 20
           :gap 20))

(q/defsketch simple-mixer-demo
  :title "Simple Mixer Demo"
  :size [600 400]
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