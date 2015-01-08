(ns workbench)


(defn destruct
  [l]
  (let  [[l1 l2 & l3] l]
    (println l1 l2 l3)))

(destruct ["x1" "x2" "x3" "x4" "x5"])

(println "hello")
