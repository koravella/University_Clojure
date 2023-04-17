(ns tasks.task3.src3-1)

(defn antider0 [f h]
  (letfn [(integral [x]
            (if (< x 0)
              (throw (Exception. "Less than zero"))
              (-> (/ (+ (f 0) (f (* h (int (/ x h))))) 2)
                  (+ (reduce (fn [acc k] (+ acc (f (* h k))))
                             0 (range 1 (int (/ x h)))))
                  (* h)
                  (+ (* (/ (+ (f x) (f (* h (int (/ x h))))) 2)
                        (- x (* h (int (/ x h)))))))))]
    integral))

(def mem-node-itg
  (memoize (fn [f step h]
             (if (= step 0)
               0
               (+
                 (-> (+ (f (* h step)) (f (* h (dec step))))
                     (* h)
                     (/ 2))
                 (mem-node-itg f (dec step) h))))))

(defn antider1 [f h]
  (fn [x]
    (if (< x 0)
      (throw (Exception. "Less than zero"))
      (let [n (int (/ x h))]
        (+
          (-> (+ (f (* h n)) (f x))
              (* (- x (* h n)))
              (/ 2))
          (mem-node-itg f n h)))
      )))

(defn graph [f h x]
  (doall (map f (range 0 x h))))

(defn -main [& args]
  (let [h 0.00917, f #(* % %), end 100
        f0 (antider0 f h)
        f1 (antider1 f h)]
    ;(f1 h)
    (println (time (f0 1)))
    (println (time (f0 1)))
    (println (time (f0 1.01)))
    (println (time (f0 1.5)))
    (println (time (f1 1)))
    (println (time (f1 1)))
    (println (time (f1 1.01)))
    (println (time (f1 1.5)))
    ;(println "Non-memoized realization 1:")
    ;(time (graph f0 h end))
    ;(time (graph f0 h end))
    ;(println "Memoized realization:")
    ;(time (graph f1 h end))
    ;(time (graph f1 h end))
    ))


