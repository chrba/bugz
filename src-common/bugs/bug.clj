(ns bugs.bug
  (:require [bugs.math :as m]))

(def turn-velocity 40)
(def near-target 0.3)
(def velocity 18)

(declare turn-to-target move-forward move waiting moving)

(defn create-bug
  [pos orientation]
  {:speed 0
   :pos pos
   :orientation orientation
   :waiting? true
   :food 0
   :home [26 26]
   :to-destination pos
   :spacial-mem {:target-cell nil :visited [] :seen []}
   :last nil
   ;;:motion :moving
   ;; :brain {:action 0
   ;;         :path-following {:target-cell nil :seen [] :visited []}
   ;;         :target-pos  pos
   ;;         :target-orientation orientation
   ;;         :home pos
   ;;         }
   })

(defn set-destination
  [bug pos]
  (assoc bug :to-destination pos))

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

(defn walk-to-destination
  [{:keys [delta-time] :as dt} {:keys [to-destination] :as bug}]
  (move dt bug to-destination))

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
       :last bug
       :pos 
            [(+ x  x-change)
             (+ y  y-change)])))

(defn- turn
  "turns the bug in the given direction. Direction should be :left or :right"
  [{:keys [orientation] :as bug} direction]
  (let [change {:left #(- % turn-velocity) :right #(+ % turn-velocity)}]
    (assoc bug :orientation ((get change direction identity) orientation))))


(defn- turn-to-target
  [{:keys [orientation] :as bug} to-orientation]
  (let [dist (m/angle-dist orientation to-orientation)]
    (cond
     (>= turn-velocity (Math/abs dist)) (assoc bug :orientation to-orientation)
     (< dist 0) (turn bug :left)
     :else (turn bug :right))))

(defn- move
  [{:keys [delta-time] :as dt} {:keys [pos orientation] :as bug} to-pos]
  (let [
        vec-to-orientation (map - to-pos pos)
        to-orientation (m/angle [1 0] vec-to-orientation)
        turned-bug (turn-to-target bug to-orientation)
        near-target? (< (m/dist to-pos pos) near-target)
    ;;    rotate-only? (and (not= orientation to-orientation) (< (m/dist to-pos pos) 5))
        ]
    (if near-target?
      (assoc bug :pos to-pos :waiting? true)
      (assoc (move-forward dt turned-bug) :waiting? false))))



(defn carries-food?
  [{:keys [food] :as bug}]
  (> food 0))



(comment (defn go-home
   [bug]
   (let [home {:pos (bug :home)}]
     (move-to-pos bug home))))
