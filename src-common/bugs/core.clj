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
                           :num 5)
              enemy-imgs (u/create-sprite
                          :img "enemy.png"
                          :split-x 50 :split-y 40
                          :width 1.5 :height 1.5
                          :num 4)
              food-imgsx (u/create-sprite
                         :img "food2.png"
                         :split-x 30 :split-y 28
                         :width 0.4 :height 0.4
                         :num 5)
              home      (assoc (texture "home.png")
                          :home? true :x 1 :y 2)
              
              food (e/create-food food-imgsx 5 1 0)
              player (e/create-player player-imgs)
              enemies (e/create-enemies enemy-imgs)]
          (flatten [home player enemies food])))
  
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
    (let [player (->> (find-first :player? entities)
                      (e/update-player-movement)
                      (e/kill-player entities))
          
          enemies (->> (filter :enemy? entities)
                       (map (fn [entity] (->> entity
                                              (e/update-enemy-movement screen)
                                              (e/attack player)))))
          
          food (->> (filter :food? entities)
                    (e/take-food player)
                    (e/drop-food player))]
      
      (->> (flatten [enemies player food])
           (map (fn [entity]
                  (->> entity
                       (b/move-forward screen)
                       (prevent-move screen)
                       (e/animate-moving screen))))
           (render! screen)
           (update-screen! screen)))))

(defgame bugs
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
