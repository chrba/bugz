(ns bugs.entity
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.bug :as b]
            [bugs.utils :as u]
            [bugs.gui :as g]
            [bugs.math :as m]))


(defn animate-moving
  [screen {:keys [killed walk stand animation] :as entity}]
   (cond
    (:killed? entity) (merge entity killed)
    (:waiting? entity) (merge entity stand)
    (:animated? entity) (merge entity (animation->texture screen animation))
    :else (merge entity (animation->texture screen walk))))


(defn create-food
  [imgs x y angle]
  (let [entity {:x x :y y :angle 0 :food? true}
        [stand & other] imgs
        anim (assoc stand
               :animated? true
               :animation (animation 0.2 other :set-play-mode
                                     (play-mode :loop-pingpong)))]
    (merge anim entity)))



(defn take-food
  [entity foods]
  (let [picked (find-first #(< (u/dist entity %) 1) foods)
        other (remove #{picked} foods)]
    (if (not (nil? picked))
      (conj other (assoc picked
                    :x (:x entity)
                    :y (:y entity)
                    :picked? true))
      foods)))


(defn drop-food
  [entity foods]
 (let [picked (find-first :picked? foods)]
   (if (and
        (not (nil? picked))
        (< (u/dist entity (:home entity)) 1))
     (remove #{picked} foods)
     foods)))

(defn create-entity
  [imgs x y angle]
  (let [entity (b/create-bug x y angle)
        [killed stand & walk] imgs
        anim (assoc stand
               :walk (animation 0.05 walk :set-play-mode (play-mode :loop-pingpong))
               :stand stand
               :killed killed)]
    (merge anim entity)))


(defn create-enemies
  [imgs]
  (letfn [(create [imgs & pos]
            (for [[x y angle] pos]
              (assoc (create-entity imgs x y angle)
                :enemy? true)))]
    (for [i (range 10)]
      (create imgs [5 8 1]))))


(defn create-player
  [imgs]
 (let [player (create-entity imgs 5 0 0)]
   (assoc player
     :player? true
     :home {:x 1 :y 1})))





(defn update-player-movement
  [entity]
  (if (:player? entity)
    (cond
     (:killed? entity) entity
     (and (key-pressed? :dpad-right) (key-pressed? :dpad-up))
     (b/set-moving entity -45)
     (and (key-pressed? :dpad-up) (key-pressed? :dpad-left))
     (b/set-moving entity 45)
     (and (key-pressed? :dpad-left) (key-pressed? :dpad-down))
     (b/set-moving entity 135)
     (and (key-pressed? :dpad-down) (key-pressed? :dpad-right))
     (b/set-moving entity 235)
     (key-pressed? :dpad-right) (b/set-moving entity -90)
     (key-pressed? :dpad-up) (b/set-moving entity 0)
     (key-pressed? :dpad-left) (b/set-moving entity 90)
     (key-pressed? :dpad-down) (b/set-moving entity 180)
     :else (b/set-waiting entity))
    entity))

(defn kill-player
  [entities entity]
  (if (:player? entity)
    (let [player entity
          enemies (filter :enemy? entities)
          dists (map #(m/dist
                       [(:x player) (:y player)]
                       [(:x %1) (:y %1)])
                     enemies)]
      (if (some #(< % u/kill-dist) dists)
        (assoc player :killed? true)
        player))
    entity))

(defn update-enemy-movement
  [{:keys [delta-time] :as dt} bug]
  (if (:enemy? bug)
    (let [next-dest? (<  (rand-int 100) 5)]
      (cond
       (:player? bug) bug
       (:eating? bug) (b/set-waiting bug)
       next-dest? (b/set-moving bug (rand-int 360))
       :else bug))
      bug))


(defn attack
  [player bug]
  (if (:enemy? bug)
      (let [v1 [(:x player) (:y player)]
            v2 [(:x bug) (:y bug)]
            dist (m/dist v1 v2) ]
        (cond
         (< dist u/kill-dist) (assoc bug :eating? true)
         (< dist u/attack-dist)
          (let [vec (map - v2 v1)
                to-orientation (m/angle [0 -1] vec)]
            (assoc bug :angle to-orientation))
          :else bug))
      bug))

