(ns bugs.utils
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.gui :as gui]
            [bugs.bug :as b]))

(def gamewidth 40)
(def gameheight 40)


(defn create-sprite
  [& {:keys [img split-x split-y width height num]}]
  (let [sheet (texture img)
        tiles (texture! sheet :split split-x split-y)
        images (for [col (range num)]
                 (assoc (texture (aget tiles 0 col))
                   :width width :height height))]                   
    images))



