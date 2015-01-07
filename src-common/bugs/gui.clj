(ns bugs.gui
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.bug :as b :refer [create-bug]]))

(declare with-animation)

;creates an animated entity     
(defn create-entity
  [imgs x y angle]
  (let [
        me (b/create-bug x y angle)
        entity (apply with-animation imgs)]
    (merge entity me)))
 
;returns an entity with one animate step applied: must
;be called on every tick
(defn animate
  [screen {:keys [killed walk stand] :as entity}]
   (cond
    (:killed? entity) (merge entity killed)
    (:waiting? entity) (merge entity stand)
    :else (merge entity (animation->texture screen walk))))

;crates a libgdx animation with the given images
(defn- with-animation
  [killed stand & walk]
  (assoc stand
    :walk (animation 0.05 walk :set-play-mode (play-mode :loop-pingpong))
    :stand stand
    :killed killed))
