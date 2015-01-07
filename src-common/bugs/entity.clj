(ns bugs.entity
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.bug :as b]
            [bugs.utils :as u]
            [bugs.gui :as g]
            [bugs.math :as m]))


(defn create-enemies
  [imgs]
  (letfn [(create [imgs & pos]
            (for [[x y angle] pos]
              (g/create-entity imgs x y angle)))]
    (for [i (range 1)]
      (create imgs [5 8 1]))))


(defn rand-enemy-dest
  [{:keys [delta-time] :as dt} bug]
  (let [next-dest? (<  (rand-int 100) 5)]
    (cond
     (:player? bug) bug
     next-dest? (b/set-moving bug (rand-int 360))
     :else bug)))


(defn attack
  [player bug]
  (if (:player? bug) bug
      (let [v1 [(:x player) (:y player)]
            v2 [(:x bug) (:y bug)]
            dist (m/dist v1 v2) ]
        (if (< dist u/attack-dist)
          (let [vec (map - v2 v1)
                to-orientation (m/angle [0 -1] vec)]
            (assoc bug :angle to-orientation))
          bug))))
