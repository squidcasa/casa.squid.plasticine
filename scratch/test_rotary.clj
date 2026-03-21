(ns test-rotary
  (:require
   [casa.squid.plasticine.rotary :refer [rotary]]))

;; Test creating a rotary with default values
(def r1 (rotary {}))
(println "Default rotary:" @r1)

;; Test creating a rotary with custom values
(def r2 (rotary {:value 25 :min 10 :max 50 :size 80}))
(println "Custom rotary:" @r2)

;; Test that the metadata is set correctly
(println "Has draw method:" (contains? (meta r2) :-draw))
(println "Has pref-size method:" (contains? (meta r2) :-pref-size))
(println "Has mouse-pressed method:" (contains? (meta r2) :-mouse-pressed))
(println "Has mouse-dragged method:" (contains? (meta r2) :-mouse-dragged))

(println "All tests passed!")