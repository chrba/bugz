(ns bugs.entity
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.bug :as b]
            [bugs.utils :as u]
            [bugs.gui :as g]))


(defn create-enemies
  [imgs]
  (letfn [(create [imgs & pos]
            (for [[x y angle] pos]
              (g/create-entity imgs [x y] angle)))]
    (for [i (range 4)]
      (create imgs [5 8 1]))))


(defn rand-enemy-dest
  [{:keys [delta-time] :as dt} bug]
  (let [next-dest? (<  (rand-int 100) 5)]
    (cond
     (:player? bug) bug
     next-dest? (b/set-moving bug (rand-int 360))
     :else bug)))
