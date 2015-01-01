(ns bugs.math
  (:require [clojure.contrib.math :as m]))

(defn radians
  "Convers degrees to radians"
  [deg]
  (Math/toRadians deg))

(defn degrees
  "Convers radians to degees"
  [rad]
  (Math/toDegrees rad))




(defn length
  "The length of the given vector"
  [v]
  (m/sqrt (reduce + (map #(m/expt % 2) v))))

(defn normalize
  "Returns the normalized vector"
  [v]
  (let [len (length v)
        fac (take 2 (repeat (/ 1 len)))]
    (map * fac v)))


(defn direction
  "The normalized direction from v1 to v2"
  [v1 v2]
  (normalize(map - v2 v1)))


(defn angle
  "The angle between the two vectors in degrees"
  [a b]
  (let [[ax ay] a
        [bx by] b
        rad (- (Math/atan2 ay ax) (Math/atan2 by bx))]
    (int (mod (- 360 (degrees rad)) 360))))



(defn angle-dist
  "The smallest distance between two angles in degrees, may be negative,
   e.g. the distance between 1 and 359 is -2."
  [a1 a2]
  (let [a1 (mod a1 360)
        a2 (mod a2 360)
        angle (mod (Math/abs(- a1 a2)) 360)
        abs-dist (if (> angle 180) (- 360 angle) angle)]
    (cond
     (= a2 (+ a1 abs-dist)) abs-dist
     (= (+ 360 a2) (+ a1 abs-dist)) abs-dist
     :else (* -1 abs-dist))))



(defn dist
  "the distance between two vectors"
  [v1 v2]
  (m/sqrt
   (reduce + (map #(m/expt (- % %2) 2)  v2 v1))))


(defn orientation
  "Returns the orientation of the given vector in degrees"
  [v]
  (angle [1 0] v))
