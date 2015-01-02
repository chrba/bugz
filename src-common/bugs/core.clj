(ns bugs.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.gui :as gui]
            [bugs.bug :as b]))

(defn update-entity
  [bug]
  (assoc bug
    :x (get-in bug [:pos 0])
    :y (get-in bug [:pos 1])
    :angle (- (:orientation bug) 90)
    :me? true))

(defn update-screen!
  [screen entities]
  (doseq [{:keys [x y me?]} entities]
    (when me?
      (position! screen 20 (/ 20 2))
      )
    )
  entities)


(defscreen main-screen
  :on-show
  (fn [screen entities]
       (->> (orthogonal-tiled-map "level1.tmx" (/ 1 16))
            (update! screen :camera (orthographic) :renderer))
        (comment (update! screen :renderer (stage)))
        (let [sheet (texture "bug-sprite.png")
              tiles (texture! sheet :split 50 45)
              player-imgs (for [col [0 1 2 3]]
                            (assoc (texture (aget tiles 0 col))
                              :width 2 :height 2))]                   
          (gui/create-entity player-imgs [5 0] 0)))
  
  :on-touch-down
  (fn [screen entities]
    (let [me (first entities)
          coord (input->screen screen (:input-x screen) (:input-y screen))
          pos [(:x coord) (:y coord)]]
      (b/set-destination me pos)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen 20))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (map (fn [entity]
                (->> entity
                     (update-entity)
                     (gui/animate screen)
                     (b/walk-to-destination screen))))
         (render! screen)
         (update-screen! screen)))



)

(defgame bugs
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
