(ns bugs.bug
  (:require [bugs.math :as m]))

(def bug-speed 6)
(def turn-speed 25)

(declare move)
(declare waiting)
(declare moving)

(defn create-bug
  [pos orientation]
  {:max-speed 4
   :speed 80
   :type :bug
   :pos pos
   :orientation orientation
   :action :moving
   :food 0
   :home [26 26]
   :to-destination pos
   :spacial-mem {:target-cell nil :visited [] :seen []}
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

(defn walk-to-destination
  [{:keys [delta-time] :as dt} {:keys [to-destination] :as bug}]
  (move dt bug to-destination))

(defn move-forward  
  "moves the bug on step in the direction of the orientation"
  [{:keys [delta-time]} {:keys [pos orientation speed] :as bug}]
  (let [[x y] pos
        x-change (* delta-time  speed (Math/cos (m/radians orientation)))
        y-change (* delta-time speed (Math/sin (m/radians orientation)))]
     (assoc bug :pos 
            [(+ x  x-change)
             (+ y  y-change)])))

(defn turn
  "turns the bug in the given direction. Direction should be :left or :right"
  [{:keys [orientation] :as bug} direction]
  (let [change {:left #(- % turn-speed) :right #(+ % turn-speed)}]
    (assoc bug :orientation ((get change direction identity) orientation))))




(defn turn-to-target
  [{:keys [orientation] :as bug} to-orientation]
  (let [dist (m/angle-dist orientation to-orientation)]
    (cond
     (>= turn-speed (Math/abs dist)) (assoc bug :orientation to-orientation)
     (< dist 0) (turn bug :left)
     :else (turn bug :right))))

;;TODO: when the target is almost reached
;;the bug should rotate until it reaches its final orientation
;;without moving forward
(defn move
  [{:keys [delta-time] :as dt} {:keys [pos orientation] :as bug} to-pos]
  (let [
        vec-to-orientation (map - to-pos pos)
        to-orientation (m/angle [1 0] vec-to-orientation)
        turned-bug (turn-to-target bug to-orientation)
        near-target? (< (m/dist to-pos pos) 3)
    ;;    rotate-only? (and (not= orientation to-orientation) (< (m/dist to-pos pos) 5))
        ]
    (if near-target?
      (waiting (assoc bug :pos to-pos))
      (moving (move-forward dt turned-bug)))))


(defn waiting
  [bug]
  (assoc bug :action :waiting))

(defn waiting?
  [bug]
  (= (:action bug) :waiting))

(defn moving
  [bug]
  (assoc bug :action :moving))

(defn moving?
  [bug]
  (= (bug :action) :moving))


(defn carries-food?
  [{:keys [food] :as bug}]
  (> food 0))


;; moves the bug and updates state info
(comment (defn move-to-pos
   [{:keys [pos orientation] :as bug}  {to-pos :pos}]
   (cond
    (= pos to-pos) (waiting bug)
    :else (moving (move bug to-pos))
    )))

(comment (defn go-home
   [bug]
   (let [home {:pos (bug :home)}]
     (move-to-pos bug home))))
