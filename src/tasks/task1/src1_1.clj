(ns tasks.task1.src1-1)

(let
  [alp (list "a" "b" "c" "d"),
   n 3]
  (defn foo [cur-sym cur-str]
    (if (= (.length cur-str) n)
      (list cur-str)
      (letfn [(iter [coll]
                (if (> (count coll) 0)
                  (concat
                    (if (not= cur-sym (first coll))
                      (foo (first coll) (str cur-str (first coll))))
                    (iter (rest coll)))))]
        (iter alp))))
  (println (foo nil "")))
