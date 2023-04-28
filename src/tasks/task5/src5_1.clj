(ns tasks.task5.src5-1)

(def num-threads 8)

(defn my-partition [num coll]
  (let [size (Math/ceil (/ (count coll) num))]
    (reverse
      (cons
        (drop (* size (- num 1)) coll)
        (first
          (reduce (fn [[acc rest-coll] _]
                    [(cons (take size rest-coll) acc)
                     (drop size rest-coll)])
                  [(list) coll]
                  (range 0 (- num 1))))))))

(defn my-pfilter [pred coll]
  (apply concat
         (map deref
              (doall (map #(future (doall (filter pred %)))
                          (my-partition num-threads coll))))))

(defn heavy-odd? [num]
  (Thread/sleep 10)
  (odd? num))

(defn -main [& args]
  (println (time (doall (filter heavy-odd? (take 100 (iterate inc 0))))))
  (println (time (my-pfilter heavy-odd? (take 100 (iterate inc 0))))))