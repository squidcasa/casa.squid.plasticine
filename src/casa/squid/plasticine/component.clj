(ns casa.squid.plasticine.component
  "Methods implemented by components, with fallback implementations, and related
  helper functions."
  (:require
   [casa.squid.plasticine.draw :as draw]
   [casa.squid.plasticine.object :as o]
   [quil.core :as q]))

(def ^:dynamic *drawing-component* nil)

(defn find-root [c]
  (if-let [p (:parent (meta c))]
    (recur p)
    c))

(defn mark-dirty! [c]
  (alter-meta! (find-root c) assoc :dirty? true)
  c)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Interface

(o/defdispatch -draw [this x y w h])

;; Constraint-based layout interface
(o/defdispatch -layout-size
  "Calculate size for this component given constraints [min-width max-width min-height max-height].
   Returns [width height] that fits within constraints.

   Components may have :flex-width and/or :flex-height attributes (default 0) to indicate
   how they should grow when extra space is available."
  [this constraints]
  [100 100])

;; Intrinsic sizing methods
(o/defdispatch -min-intrinsic-width
  "Absolute minimum width this child needs to not break."
  [this height] 0)

(o/defdispatch -max-intrinsic-width
  "Ideal width for this child."
  [this height] Long/MAX_VALUE)

(o/defdispatch -min-intrinsic-height
  "Absolute minimum height this child needs to not break."
  [this width] 0)

(o/defdispatch -max-intrinsic-height
  "Ideal height for this child."
  [this width] Long/MAX_VALUE)

;; Legacy methods (deprecated)

(o/defdispatch -cleanup [this]
  (doseq [c (if-let [c (:child @this)]
              [c]
              (:children @this))]
    (-cleanup c)))

(o/defdispatch -key-pressed [this]
  (let [keymap (:key-pressed-map (meta this))]
    (when-let [f (get keymap (q/key-as-keyword)
                      (get keymap (q/key-code)))]
      (f this))))

(o/defdispatch -key-released [this])
(o/defdispatch -key-typed [this])

(o/defdispatch on-model-changed [this old new]
  (when (not= old new)
    (mark-dirty! this)))

(defn delegate [c f & args]
  (let [cval (with-meta @c (meta c))]
    (if-let [props (:props cval)]
      (draw/with-props props
        (apply f cval args))
      (apply f cval args))))

(defn draw [c x y w h]
  ;;(print ".") (flush) ;; see if we're not calling draw too often
  (alter-meta! c assoc :parent *drawing-component*)
  (let [root (find-root c)]
    (when (and (:focusable? (meta c))
               (not (:focused (meta root))))
      (alter-meta! root assoc :focused c)))
  (swap! c assoc :outer-bounds [x y w h])
  (let [[x y w h] (if-let [m (:margin @c)]
                    [(+ x m) (+ y m) (- w m m) (- h m m)]
                    [x y w h])]
    (swap! c assoc :bounds [x y w h])
    (binding [*drawing-component* c]
      (delegate c -draw x y w h)))
  (add-watch c ::rerender (fn [k r o n] (on-model-changed c o n))))



;; Flex attribute helpers
(defn flex-width [c]
  (let [m (if (instance? clojure.lang.Atom c) @c c)]
    (or (:flex-width m) (:flex m) 0)))

(defn flex-height [c]
  (let [m (if (instance? clojure.lang.Atom c) @c c)]
    (or (:flex-height m) (:flex m) 0)))

;; Constraint-based layout functions
(defn layout-size [c constraints]
  (delegate c -layout-size constraints))

(defn min-intrinsic-width [c height]
  (delegate c -min-intrinsic-width height))

(defn max-intrinsic-width [c height]
  (delegate c -max-intrinsic-width height))

(defn min-intrinsic-height [c width]
  (delegate c -min-intrinsic-height width))

(defn max-intrinsic-height [c width]
  (delegate c -max-intrinsic-height width))

(defn width [c]
  (or (:width c) (nth (:bounds c) 2)))

(defn height [c]
  (or (:height c) (nth (:bounds c) 3)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers

(defn dispatch-mouse-event [c e]
  ;; dispatch both a generic `:-mouse-event`, and a specific event e.g.
  ;; `:-mouse-clicked`. Components can implement one or the other. The generic
  ;; event is convenient for implementing forwarding logic.
  (o/dispatch c :-mouse-event e)
  (o/dispatch c (keyword (str "-" (name (:type e)))) e))

(defn forward-mouse-event [parent e]
  (let [{:keys [child children]} @parent
        {:keys [x y type]} e]
    (doseq [c (if child [child] children)
            :when (:bounds @c)
            :let [[cx cy cw ch] (:bounds @c)]]
      (when (and (<= cx x (+ cx cw))
                 (<= cy y (+ cy ch)))
        (dispatch-mouse-event c e)))))

(defn add! [component child]
  (swap! component update :children (fnil conj []) child))
