(ns tasks.task1.src1-3)

(defn my-map [f coll]
  (reduce (fn [x y] (concat x (list (f y))))
          (list)
          coll))

(defn my-filter [pred coll]
  (reduce (fn [x y]
            (concat
              x
              (if (pred y)
                (list y)
                (list))))
          (list)
          coll))

(println (my-map inc (list 0 1 2 3 4)))
(println (my-filter (fn [x] (= 0 (mod x 2))) (list 0 1 2 3 4 5 6 7 8 9)))