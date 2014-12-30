(ns bugs.core.desktop-launcher
  (:require [bugs.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. bugs "bugs" 800 600)
  (Keyboard/enableRepeatEvents true))
