(ns bugs.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.gui :as gui]
            [bugs.bug :as b]
            [bugs.entity :as e]
            [bugs.utils :as u]))


(defn update-entity
  [bug]
  (assoc bug
    :x (get-in bug [:pos 0])
    :y (get-in bug [:pos 1])
    :angle (- (:orientation bug) 90)))



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


(defn update-player-movement
  [entity]
  (if (:player? entity)
    (cond
     (and (key-pressed? :dpad-right) (key-pressed? :dpad-up))
     (b/set-moving entity 45)
     (and (key-pressed? :dpad-up) (key-pressed? :dpad-left))
     (b/set-moving entity 135)
     (and (key-pressed? :dpad-left) (key-pressed? :dpad-down))
     (b/set-moving entity 225)
     (and (key-pressed? :dpad-down) (key-pressed? :dpad-right))
     (b/set-moving entity 315)
     (key-pressed? :dpad-right) (b/set-moving entity 0)
     (key-pressed? :dpad-up) (b/set-moving entity 90)
     (key-pressed? :dpad-left) (b/set-moving entity 180)
     (key-pressed? :dpad-down) (b/set-moving entity 270)
     :else (b/set-waiting entity))
    entity))



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
   (> x (- u/gamewidth width))
   (< y 0)
   (> y (- u/gameheight height))))

(defn prevent-move
  [screen entity]
  (if (or
       (on-layer screen entity "obstacle")
       (illegal-position entity))
    (b/set-waiting (b/rewind entity))
    entity))




(defscreen main-screen
  :on-show
  (fn [screen entities]
       (->> (orthogonal-tiled-map "desert2.tmx" (/ 1 32))
            (update! screen :camera (orthographic) :renderer))
        (comment (update! screen :renderer (stage)))
        (let [player-imgs (u/create-sprite
                           :img "bug-sprite2.png"
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
    (let [player (update-entity (find-first :player? entities))]
      (->> entities
           (map (fn [entity]
                  (->> entity
                       (update-entity)
                       (gui/animate screen)
                       (update-player-movement)
                       (e/rand-enemy-dest screen)
                       (prevent-move screen)
                       (e/attack player)
                       (b/move-forward screen))))
           (render! screen)
           (update-screen! screen))))



  )

(defgame bugs
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
