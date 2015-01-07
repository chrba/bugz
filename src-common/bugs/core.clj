(ns bugs.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.gui :as gui]
            [bugs.bug :as b]
            [bugs.entity :as e]
            [bugs.utils :as u]))


(defn getScreenXPosition
  [screen x]
  (let [minx (/ (width screen) 2)
        maxx (- u/gamewidth minx)]
    (cond
     (< x minx) minx
     (> x maxx) maxx
     :else x)))


(defn getScreenYPosition
  [screen y]
  (let [miny (/ (height screen) 2)
        maxy (- u/gameheight miny)]
    (cond
     (< y miny) miny
     (> y maxy) maxy
     :else y)))

(defn update-screen!
  [screen entities]
  (doseq [{:keys [x y player?]} entities]
    (when player?
      (position! screen
                 (getScreenXPosition screen x)
                 (getScreenYPosition screen y))))
  entities)



(defn prevent-move
  [screen entity]
  (if (or
       (:killed? entity)
       (u/on-layer screen entity "obstacle")
       (u/illegal-position entity))
    (b/set-waiting (b/rewind entity))
    entity))


(defscreen main-screen
  :on-show
  (fn [screen entities]
       (->> (orthogonal-tiled-map "desert2.tmx" (/ 1 32))
            (update! screen :camera (orthographic) :renderer))
        (let [player-imgs (u/create-sprite
                           :img "bug-sprite3.png"
                           :split-x 50 :split-y 40
                           :width 1 :height 1
                           :num 4)
              enemy-imgs (u/create-sprite
                          :img "enemy.png"
                          :split-x 50 :split-y 40
                          :width 1.5 :height 1.5
                          :num 4)
              
              player (assoc (gui/create-entity player-imgs 5 0 0)
                       :player? true)
              enemies (e/create-enemies enemy-imgs)]
          (flatten [player enemies])))
  
;  :on-touch-down
;  (fn [screen entities]
;    (let [me (first entities)
;          coord (input->screen screen (:input-x screen) (:input-y screen))
;          pos [(:x coord) (:y coord)]]
;      (b/set-destination me pos)))

  

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen 15))

  :on-render
  (fn [screen entities]
    
    (clear!)
    (let [player (find-first :player? entities)]
      (->> entities
           (map (fn [entity]
                  (->> entity
                       (gui/animate screen)
                       (e/update-player-movement)
                       (e/update-enemy-movement screen)
                       (e/attack player)
                       (e/kill-player entities)
                       (b/move-forward screen)
                       (prevent-move screen)
                       )))
           (render! screen)
           (update-screen! screen))))



  )

(defgame bugs
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
