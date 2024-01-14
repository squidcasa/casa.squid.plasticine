(ns outline)


;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; (defn outline-draw [{:keys [child props]} x y w h]
;;   (let [sw (d/prop :stroke-weight)
;;         x (+ x (/ sw 2))
;;         y (+ y (/ sw 2))
;;         w (- w sw)
;;         h (- h sw)]
;;     (d/polygon [x y]
;;                [(+ x w) y]
;;                [(+ x w) (+ y h)]
;;                [x (+ y h)])
;;     (c/draw child
;;             (+ x (/ sw 2))
;;             (+ y (/ sw 2))
;;             (- w sw )
;;             (- h sw ))))

;; (defn outline-delegate-size [size-f]
;;   (fn [{:keys [child]}]
;;     (let [sw (d/prop :stroke-weight)
;;           [w h] (size-f child)]
;;       [(+ sw w) (+ sw h)])))

;; (def outline-meta
;;   {:-draw        #'outline-draw
;;    :-min-size    (outline-delegate-size c/min-size)
;;    :-pref-size   (outline-delegate-size c/pref-size)
;;    :-mouse-event #'c/forward-mouse-event})

;; (defn outline [child & {:as props}]
;;   (atom {:text child :props props}
;;         :meta outline-meta))


;;;;;;;;;;;;;;;;;;;;;;;;;;
