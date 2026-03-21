(ns examples.demos.settings-panel
  (:require
   [casa.squid.plasticine :as p]
   [casa.squid.plasticine.rotary :refer [rotary]]
   [casa.squid.plasticine.sliders :refer [hslider]]
   [casa.squid.plasticine.select-list :refer [select-list]]
   [quil.core :as q]))

;; Settings state
(def settings-state (atom {:volume 80
                           :brightness 70
                           :theme :dark
                           :notifications true
                           :auto-save false}))

;; Available themes
(def themes [:light :dark :blue :green])

;; Function to create a toggle switch
(defn toggle-switch [label state-key]
  (let [toggle-state (atom (get @settings-state state-key))
        toggle-component (atom {:bounds [0 0 50 25]
                                :value @toggle-state}
                               :meta {:focusable? true
                                      :-draw (fn [this x y w h]
                                               (let [value (:value @this)]
                                                 (q/stroke 150)
                                                 (q/stroke-weight 1)
                                                 (q/fill (if value [100 200 100] [200 200 200]))
                                                 (q/rect x y w h 10)
                                                 (q/fill 255)
                                                 (q/ellipse (+ x (if value (- w 15) 15)) (+ y (/ h 2)) 15 15)))
                                      :-mouse-pressed (fn [this {:keys [x y]}]
                                                       (let [[bx by bw bh] (:bounds @this)]
                                                         (when (and (<= bx x (+ bx bw))
                                                                    (<= by y (+ by bh)))
                                                           (let [new-value (not (:value @this))]
                                                             (swap! this assoc :value new-value)
                                                             (swap! settings-state assoc state-key new-value)))))})]
    (p/stack [(p/text label {:text-size 14})
              toggle-component]
             :gap 5)))

;; Create settings controls
(def volume-control
  (p/cols [(p/text "Volume:" {:text-size 14})
           (rotary {:model (atom (:volume @settings-state))
                    :min 0
                    :max 100
                    :size 50
                    :on-change (fn [new-value]
                                 (swap! settings-state assoc :volume new-value))})]
          :gap 10))

(def brightness-control
  (p/cols [(p/text "Brightness:" {:text-size 14})
           (hslider {:model (atom (:brightness @settings-state))
                     :min 0
                     :max 100
                     :height 20
                     :on-change (fn [new-value]
                                  (swap! settings-state assoc :brightness new-value))})]
          :gap 10))

(def theme-control
  (p/cols [(p/text "Theme:" {:text-size 14})
           (p/text (name (:theme @settings-state)) {:text-size 14})]
          :gap 10))

(def notifications-toggle (toggle-switch "Notifications" :notifications))
(def auto-save-toggle (toggle-switch "Auto Save" :auto-save))

;; State display
(def state-display
  (p/text (str "Settings: " @settings-state) {:text-size 12}))

;; Update display when state changes
(add-watch settings-state :update-display
           (fn [_ _ _ new-state]
             (swap! state-display assoc :text (str "Settings: " new-state))))

(def app
  (p/stack [(p/text "Settings Panel" {:text-size 20})
            (p/text "Application settings interface with different control types")
            volume-control
            brightness-control
            theme-control
            notifications-toggle
            auto-save-toggle
            state-display]
           :margin 20
           :gap 20))

(q/defsketch settings-panel-demo
  :title "Settings Panel Demo"
  :size [400 400]
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