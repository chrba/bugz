(ns bugs.entity
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.bug :as b]
            [bugs.utils :as u]
            [bugs.gui :as g]
            [bugs.math :as m]))


(defn create-food
  [x y]
  {:x x
   :y y
   :food? true})


(defn create-player
  [x y angle]
 (let [player (b/create-bug x y angle)]
   (assoc player :player? true)))


(defn create-enemies
  [imgs]
  (letfn [(create [imgs & pos]
            (for [[x y angle] pos]
              (g/create-entity imgs x y angle)))]
    (for [i (range 1)]
      (create imgs [5 8 1]))))



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
          enemies (remove :player? entities)
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
  (let [next-dest? (<  (rand-int 100) 5)]
    (cond
     (:player? bug) bug
     (:eating? bug) (b/set-waiting bug)
     next-dest? (b/set-moving bug (rand-int 360))
     :else bug)))


(defn attack
  [player bug]
  (if (:player? bug) bug
      (let [v1 [(:x player) (:y player)]
            v2 [(:x bug) (:y bug)]
            dist (m/dist v1 v2) ]
        (cond
         (< dist u/kill-dist) (assoc bug :eating? true)
         (< dist u/attack-dist)
          (let [vec (map - v2 v1)
                to-orientation (m/angle [0 -1] vec)]
            (assoc bug :angle to-orientation))
          :else bug))))

