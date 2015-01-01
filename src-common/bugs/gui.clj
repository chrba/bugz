(ns bugs.gui
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.bug :as b :refer [waiting? create-bug]]))

(declare with-animation)

;creates an animated entity     
(defn create-entity
  [imgs pos angle]
  (let [me (b/create-bug pos angle)
        entity (apply with-animation imgs)]
    (merge entity me)))
 
;returns an entity with one animate step applied: must
;be called on every tick
(defn animate
  [screen {:keys [walk stand] :as entity}]
  (cond
   (b/waiting? entity) (merge entity stand)
   :else (merge entity (animation->texture screen walk))))

;crates a libgdx animation with the given images
(defn- with-animation
  [stand & walk]
  (assoc stand
    :walk (animation 0.05 walk :set-play-mode (play-mode :loop-pingpong))
    :stand stand))
