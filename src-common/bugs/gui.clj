(ns bugs.gui
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [bugs.bug :as b :refer [create-bug]]))

(declare with-animation)





(defn with-moving-animation
  [imgs duration entity]
  (let [[killed stand & walk] imgs
        anim (assoc stand
               :walk (animation duration walk
                                :set-play-mode (play-mode :loop-pingpong))
               :stand stand
               :killed killed)]
    (merge anim entity)))

(defn animate-moving
  [screen {:keys [killed walk stand] :as entity}]
   (cond
    (:killed? entity) (merge entity killed)
    (:waiting? entity) (merge entity stand)
    :else (merge entity (animation->texture screen walk))))

;returns an entity with one animate step applied: must
;be called on every tick
(defn animate
  [screen {:keys [killed walk stand] :as entity}]
   (cond
    (:killed? entity) (merge entity killed)
    (:waiting? entity) (merge entity stand)
    :else (merge entity (animation->texture screen walk))))


(defn with-object-animation
  [imgs duration entity]
  (let [default (get 0 imgs)
        anim (assoc default
               :animation (animation duration imgs
                                :set-play-mode (play-mode :loop-pingpong)))]
    (merge anim entity)))


(defn animate-object
  [screen {:keys [animation] :as entity}]
  (merge entity (animation->texture screen animation)))

;creates an animated entity     
(defn create-entity
  [imgs x y angle]
  (let [
        me (b/create-bug x y angle)
        entity (apply with-animation imgs)]
    (merge entity me)))
 

;crates a libgdx animation with the given images
(defn- with-animation
  [killed stand & walk]
  (assoc stand
    :walk (animation 0.05 walk :set-play-mode (play-mode :loop-pingpong))
    :stand stand
    :killed killed))
