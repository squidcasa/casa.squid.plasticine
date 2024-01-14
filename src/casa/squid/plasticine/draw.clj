(ns casa.squid.plasticine.draw
  (:require
   [quil.core :as q]))

(def ^:dynamic *stack* {})

(def quil-setter
  (memoize
   (fn [k]
     (resolve (symbol "quil.core" (name k))))))

(def quil-getter
  (memoize
   (fn [k]
     (or
      (resolve (symbol "quil.core" (str "current-" (name k))))
      (throw (Exception. (str "no such getter" k)))))))

(defn prop
  ([k]
   (get *stack* k))
  ([k not-found]
   (get *stack* k not-found)))

(defn set-prop! [k v]
  (if (vector? v)
    (apply (quil-setter k) v)
    ((quil-setter k) v)))

(defmacro with-props [props & body]
  `(let [old# *stack*
         new# ~props
         ks# (filter #(not= (get old# %) (get new# %)) (keys new#))]
     (doseq [k# ks#
             :let [v# (get new# k#)]]
       (set-prop! k# v#))
     (let [res# (binding [*stack* (merge old# new#)]
                  ~@body)]
       (doseq [k# ks#
               :when (contains? old# k#)
               :let [v# (get old# k#)]]
         (set-prop! k# v#))
       res#)))

(defn init-props! [props]
  (doseq [[k v] props]
    (set-prop! k v))
  (alter-var-root #'*stack* (constantly props)))

(defn polygon [& points]
  (doseq [[[x1 y1] [x2 y2]]
          (partition 2 1 (cons (last points) points))]
    (q/line x1 y1 x2 y2)))

(defn border-rect [x y w h]
  (q/rect (+ x (prop :stroke-weight))
          (+ y (prop :stroke-weight))
          (- w (prop :stroke-weight) (prop :stroke-weight))
          (- h (prop :stroke-weight) (prop :stroke-weight))))
