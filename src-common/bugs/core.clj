(ns bugs.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.gui :as gui]
            [bugs.bug :as b]))

(defn update-entity
  [bug]
  (assoc bug
    :x (- (get-in bug [:pos 0]) 25)
    :y (- (get-in bug [:pos 1]) 25)
    :angle (- (:orientation bug) 90)
    :me? true))

(defn update-screen!
  [screen entities]
  (doseq [{:keys [x y me?]} entities]
    (when me?
      (position! screen x (/ 20 2))
      )
    )
  entities)


(defscreen main-screen
  :on-show
  (fn [screen entities]
       (comment (->> (orthogonal-tiled-map "level1.tmx" (/ 1 16))
             (update! screen :camera (orthographic) :renderer)))
        (update! screen :renderer (stage))
        (let [sheet (texture "bug-sprite.png")
              tiles (texture! sheet :split 50 45)
              player-imgs (for [col [0 1 2 3]]
                            (texture (aget tiles 0 col)))]                   
          (gui/create-entity player-imgs [20 20] 0)))
  
  :on-touch-down
  (fn [screen entities]
    (let [me (first entities)
          pos [(:input-x screen) (- (height screen) (:input-y screen))]]
      (b/set-destination me pos)))



  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (comment (height! screen 20)))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (map (fn [entity]
                (->> entity
                     (update-entity)
                     (gui/animate screen)
                     (b/walk-to-destination))))
         (render! screen)))



)

(defgame bugs
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
