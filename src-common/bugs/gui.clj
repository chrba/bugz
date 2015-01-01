(ns bugs.gui
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn with-animation
  [stand & walk ]
  (assoc stand
    :walk (animation 0.05 walk :set-play-mode (play-mode :loop-pingpong))
    :wait stand))
