(ns casa.squid.plasticine.util)

(defn import-publics [target-ns ns-names]
  (doseq [n ns-names
          v (map val (ns-publics (doto n require)))
          :let [m (meta v)]]
    (intern *ns* (with-meta (:name m) (meta v)) @v)))

(defn clamp [i x a]
  (max i (min x a)))

(defn clamped+
  ([a b]
   (min Long/MAX_VALUE (+' a b)))
  ([a b c]
   (min Long/MAX_VALUE (+' a b c))))
