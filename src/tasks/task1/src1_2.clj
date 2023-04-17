(ns tasks.task1.src1-2)

(defn words
  ([alp n] (if (= n 0) (list) (words alp n alp 1)))
  ([alp n all-words step]
   (letfn [(_words
             ([input-words] (_words input-words (list)))
             ([input-words res-words]
              (letfn [(iter-alp
                        ([input-str] (iter-alp input-str alp (list)))
                        ([input-str input-alp res-coll-str]
                         (if (> (count input-alp) 0)
                           (recur input-str (rest input-alp)
                                  (concat res-coll-str
                                          (if (not= (first input-alp)
                                                    (.substring input-str (- (.length input-str) 1)))
                                            (list (str input-str (first input-alp))))))
                           res-coll-str)))]
                (if (> (count input-words) 0)
                  (recur (rest input-words) (concat res-words (iter-alp (first input-words))))
                  res-words))))]
     (if (= step n) all-words (recur alp n (_words all-words) (+ step 1)))))
  )

(println (words (list "a" "b" "c" "d") 3))