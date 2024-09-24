(ns casa.squid.plasticine.util)

(defn import-publics [target-ns ns-names]
  (doseq [n ns-names
          v (map val (ns-publics (doto n require)))
          :let [m (meta v)]]
    (intern *ns* (with-meta (:name m) (meta v)) @v)))
