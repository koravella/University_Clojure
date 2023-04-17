(ns tasks.task1.src1-4)

(defn words [alp n]
  (reduce
    (fn [cur-words cnt]
      (if (= cnt 0)
        alp
        (reduce
          (fn [res-words word]
            (concat res-words
              (let [sym (.substring word (- (.length word) 1))]
                (reduce
                  (fn [new-words new-sym]
                    (concat new-words
                      (if (not= sym new-sym) (list (str word new-sym)))))
                  (list) alp))))
          (list) cur-words)))
    (list) (range 0 n)))

(println (words (list "a" "b" "c" "d") 3))