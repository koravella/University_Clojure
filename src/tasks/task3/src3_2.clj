(ns tasks.task3.src3-2
  (:require [tasks.task3.src3-1 :as task3-1]))

(defn antider [f h]
  (let [node_integral
        (map first
             (iterate
               (fn [[val step]]
                 [(+ val (-> (+ (f (* h step)) (f (* h (inc step)))) (* h) (/ 2))) (inc step)])
               [0 0]))]
    (fn [x]
      (if (< x 0)
        (throw (Exception. "Less than zero"))
        (let [n (int (/ x h))]
          (+
            (-> (+ (f (* h n)) (f x)) (* (- x (* h n))) (/ 2))
            (nth node_integral n)))))))


(defn -main [& args]
  (let [h 0.00917, f #(* % %), end 50
        f1 (antider f h)
        f0 (task3-1/antider0 f h)]
    (f1 h)
    ;(time (f0 10000))
    (time (f1 1))
    (time (f1 10000))
    (time (f1 10000))
    (time (f1 10000.01))
    (time (f1 15000))
    ;(println "Non-memoized realization 1:")
    ;(time (task3-1/graph f0 h end))
    ;(println "Stream realization:")
    ;(time (task3-1/graph f1 h end))
    ;(time (task3-1/graph f1 h end))
    ))
