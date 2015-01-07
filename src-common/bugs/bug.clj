(ns bugs.bug
  (:require [bugs.math :as m]))

(def turn-velocity 40)
(def near-target 0.3)
(def velocity 18)

(declare turn-to-target move-forward move waiting moving)

(defn create-bug
  [x y orientation]
  {:speed 0
   :pos [x y]
   :orientation orientation
   :waiting? true
   :player? false
   :last nil
   })


(defn set-moving
  [bug orientation]
  (assoc bug
    :waiting? false
    :orientation orientation
    :speed velocity))

(defn set-waiting
  [bug]
  (assoc bug
    :waiting? true
    :to-orientation (:orientation bug)
    :speed 0))


(defn- remove-last
  [bug]
  (assoc bug :last nil))




(defn rewind
  [bug]
  (:last bug))

(defn move-forward  
  "moves the bug on step in the direction of the orientation"
  [{:keys [delta-time]} {:keys [pos orientation speed] :as bug}]
  (let [[x y] pos
        x-change (* delta-time  speed (Math/cos (m/radians orientation)))
        y-change (* delta-time speed (Math/sin (m/radians orientation)))]
     (assoc bug
       :last (remove-last bug)
       :pos 
            [(+ x  x-change)
             (+ y  y-change)])))




