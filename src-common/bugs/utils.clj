(ns bugs.utils
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.math :as m]
            [bugs.gui :as gui]
            [bugs.bug :as b]))

(def gamewidth 40)
(def gameheight 40)
(def attack-dist 5)
(def kill-dist 1)

(defn create-sprite
  [& {:keys [img split-x split-y width height num]}]
  (let [sheet (texture img)
        tiles (texture! sheet :split split-x split-y)
        images (for [col (range num)]
                 (assoc (texture (aget tiles 0 col))
                   :width width :height height))]                   
    images))



(defn on-layer
  [screen {:keys [x y width height] :as entity} layer-name]
  (let [layer (tiled-map-layer screen layer-name)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (-> (tiled-map-cell layer tile-x tile-y)
               nil?
               not))
         (some identity))))

(defn illegal-position
  [{:keys [x y width height] :as entity}]
  (or
   (< x 0) 
   (> x (- gamewidth width))
   (< y 0)
   (> y (- gameheight height))))

(defn game-over
  [player entities]
  (if (:killed? player)
    (map #(assoc % :speed 0)
         entities)
    entities))

(defn dist
 [entity1 entity2]
 (let [v1 [(:x entity1) (:y entity1)]
       v2 [(:x entity2) (:y entity2)]]
   (m/dist v1 v2)))
