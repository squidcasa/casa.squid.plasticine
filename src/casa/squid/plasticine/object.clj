(ns casa.squid.plasticine.object
  "Object abstraction

  An 'object' in plasticine is an atom with methods in its metadata map. You
  can [[dispatch]] dynamically, or define a named dispatch function
  with [[defdispatch]].

  Also contains utilities for data binding.")

(defn dispatch [c method & args]
  (when-let [f (get (meta c) method)]
    (apply f c args)))

(defn dispatch! [c method & args]
  (if-let [f (get (meta c) method)]
    (apply f c args)
    (throw (ex-info (str "No method " method " in " (keys (meta c)))
                    {:state @c
                     :meta (meta c)}))))

(defmacro defdispatch [n args & default]
  (let [kw (keyword n)]
    `(defn ~n [~@args]
       (if-let [f# (get (meta ~(first args)) ~kw)]
         (f# ~@args)
         (do ~@default)))))

(def ^:dynamic *bind-set* #{})

(defn bind>
  "One-way data binding between (subpaths of) two atoms"
  [src src-path dest dest-path]
  (let [dest-vec [dest dest-path]]
    (add-watch src [:bind> src-path dest dest-path]
               (fn [k r o n]
                 (when-not (contains? *bind-set* dest-vec)
                   (let [new-val (get-in n src-path)]
                     (when (not= new-val (get-in o src-path))
                       (binding [*bind-set* (conj *bind-set* dest-vec)]
                         (if (seq dest-path)
                           (swap! dest assoc-in dest-path new-val)
                           (reset! dest new-val))))))))))

(defn bind<>
  "Two-way data binding between (subpaths of) two atoms"
  [src src-path dest dest-path]
  (bind> src src-path dest dest-path)
  (bind> dest dest-path src src-path))

(defn unbind
  "Remove [[bind>]] or [[bind<>]] bindings"
  [src src-path dest dest-path]
  (remove-watch [:bind> src src-path dest dest-path])
  (remove-watch [:bind> dest dest-path src src-path]))
