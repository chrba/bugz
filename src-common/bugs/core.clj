(ns bugs.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.gui :as gui]
            [bugs.bug :as b]))

(defn cljify
  [bug]
  (let [me (assoc bug :x (- (get-in bug [:pos 0]) 25))
        me (assoc me :y (- (get-in bug [:pos 1]) 25))
        me (assoc me :angle (- (:orientation bug) 90))]
    me))

(defn animate
  [screen {:keys [walk wait action] :as entity}]
  (cond
   (b/waiting? entity) (merge entity wait)
   :else (merge entity (animation->texture screen walk))))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
        (let [sheet (texture "bug-sprite.png")
              tiles (texture! sheet :split 50 45)
              player-imgs (for [col [0 1 2 3]]
                            (texture (aget tiles 0 col)))
              me (b/create-bug [10 50] 0)]
          (merge (apply gui/with-animation player-imgs) me)))
  
  :on-touch-down
  (fn [screen entities]
    (let [me (first entities)
          pos [(:input-x screen) (- (height screen) (:input-y screen))]]
      (b/set-destination me pos)))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen (->> entities
                         (map cljify)
                         (map #(animate screen %))
                         (map b/walk-to-destination)))))

(defgame bugs
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
