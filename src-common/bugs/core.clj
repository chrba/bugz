(ns bugs.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.gui :as gui]
            [bugs.bug :as b]))

(def gamewidth 40)
(def gameheight 40)

(defn update-entity
  [bug]
  (assoc bug
    :x (get-in bug [:pos 0])
    :y (get-in bug [:pos 1])
    :angle (- (:orientation bug) 90)
    :me? true))



(defn getScreenXPosition
  [screen x]
  (let [minx (/ (width screen) 2)
        maxx (- gamewidth minx)]
    (cond
     (< x minx) minx
     (> x maxx) maxx
     :else x)))


(defn getScreenYPosition
  [screen y]
  (let [miny (/ (height screen) 2)
        maxy (- gameheight miny)]
    (cond
     (< y miny) miny
     (> y maxy) maxy
     :else y)))


(defn update-screen!
  [screen entities]
  (doseq [{:keys [x y me?]} entities]
    (when me?
      (position! screen
                 (getScreenXPosition screen x)
                 (getScreenYPosition screen y))
      )
    )
  entities)


(defscreen main-screen
  :on-show
  (fn [screen entities]
       (->> (orthogonal-tiled-map "desert.tmx" (/ 1 32))
            (update! screen :camera (orthographic) :renderer))
        (comment (update! screen :renderer (stage)))
        (let [sheet (texture "bug-sprite.png")
              tiles (texture! sheet :split 50 45)
              player-imgs (for [col [0 1 2 3]]
                            (assoc (texture (aget tiles 0 col))
                              :width 1 :height 1))]                   
          (gui/create-entity player-imgs [5 0] 0)))
  
  :on-touch-down
  (fn [screen entities]
    (let [me (first entities)
          coord (input->screen screen (:input-x screen) (:input-y screen))
          pos [(:x coord) (:y coord)]]
      (b/set-destination me pos)))

  :on-key-down
  (fn [{:keys [key] :as screen} entities]
    (let [player (find-first :me? entities)]
      (cond
       (= key (key-code :right)) (assoc player :speed 10)
       :else player)))

  :on-key-up
  (fn [screen entities]
    (let [player (find-first :me? entities)]
      (b/set-waiting player)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen 15))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (map (fn [entity]
                (->> entity
                     (update-entity)
                     (gui/animate screen)
                     (b/move-forward screen))))
         (render! screen)
         (update-screen! screen)))



)

(defgame bugs
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
