(ns casa.squid.plasticine
  "Moldable clay UI lib for Quil"
  (:require
   [casa.squid.plasticine.component :as c]
   [casa.squid.plasticine.draw :as d]
   [casa.squid.plasticine.util :as util]
   [quil.applet :as ap]
   [quil.core :as q]))

(import-publics
 *ns*
 '[casa.squid.plasticine.component
   casa.squid.plasticine.container
   casa.squid.plasticine.draw
   casa.squid.plasticine.object
   casa.squid.plasticine.select-list
   casa.squid.plasticine.sliders
   casa.squid.plasticine.text])

(def ^:dynamic *root* nil)

(defn- draw-root [c]
  (binding [*root* c]
    (when (or (:dirty? (meta c))
              (not= [0 0 (q/width) (q/height)] (:outer-bounds @c)))
      (alter-meta! c assoc :dirty? false)
      (d/set-prop! :background (d/prop :background))
      (c/draw c 0 0 (q/width) (q/height)))))

(defn- mouse-event-handler [c t]
  (dispatch-mouse-event
   c
   {:type t
    :x (q/mouse-x)
    :y (q/mouse-y)
    :button (q/mouse-button)
    :pressed? (q/mouse-pressed?)}))

(defn- mouse-wheel-event-handler [c i]
  (dispatch-mouse-event
   c
   {:type :mouse-wheel
    :x (q/mouse-x)
    :y (q/mouse-y)
    :button (q/mouse-button)
    :pressed? (q/mouse-pressed?)
    :wheel-rotation i}))

(defn middleware [{::keys [root defaults] :as options}]
  (c/mark-dirty! @root)
  (assoc options
         :setup          #(d/init-props! defaults)
         :draw           #(d/with-props defaults (draw-root @root))
         :on-close       #(c/-cleanup @root)
         :key-pressed    (fn []
                           (c/-key-pressed (:focused (meta @root)))
                           ;; prevent close-on-esc
                           (when (= (char 27) (.-key (ap/current-applet)))
                             (set! (.-key (ap/current-applet)) (char 0))))
         :key-released   #(c/-key-released (:focused (meta @root)))
         :key-typed      #(c/-key-typed (:focused (meta @root)))
         :key-typed      #(c/-key-typed (:focused (meta @root)))
         :mouse-entered  #(mouse-event-handler @root :mouse-entered)
         :mouse-exited   #(mouse-event-handler @root :mouse-exited)
         :mouse-pressed  #(mouse-event-handler @root :mouse-pressed)
         :mouse-released #(mouse-event-handler @root :mouse-released)
         :mouse-clicked  #(mouse-event-handler @root :mouse-clicked)
         :mouse-moved    #(mouse-event-handler @root :mouse-moved)
         :mouse-dragged  #(mouse-event-handler @root :mouse-dragged)
         :mouse-wheel    #(mouse-wheel-event-handler @root %1)))

;; - A component is an atom with metadata
;; - Functions in the metadata act as methods
;; - The atom itself is its state/model
;; - The top level component is the root and does some extra book-keeping
;;   - :dirty? on the root will cause the whole UI to redraw (as does resizing the window)
;;   - :focused is the element that will receive keyboard events
