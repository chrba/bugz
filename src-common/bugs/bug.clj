(ns bugs.bug
  (:require [bugs.math :as m]))

(def turn-velocity 40)
(def near-target 0.3)
(def velocity 18)

(declare turn-to-target move-forward move waiting moving)

(defn create-bug
  [x y angle]
  {:speed 0
   :x x 
   :y y
   :angle angle
   :waiting? true
   :player? false
   :last nil
   })


(defn set-moving
  [bug angle]
  (assoc bug
    :waiting? false
    :angle angle
    :speed velocity))

(defn set-waiting
  [bug]
  (assoc bug
    :waiting? true
    :speed 0))


(defn- remove-last
  [bug]
  (assoc bug :last nil))




(defn rewind
  [bug]
  (:last bug))

(defn move-forward  
  "moves the bug on step in the direction of the angle"
  [{:keys [delta-time]} {:keys [x y angle speed] :as bug}]
  (let [
        x-change (* delta-time  speed (Math/cos (m/radians (+ 90 angle))))
        y-change (* delta-time speed (Math/sin (m/radians (+ 90 angle))))]
     (assoc bug
       :last (remove-last bug)
       :x (+ x x-change)
       :y (+ y y-change))))




