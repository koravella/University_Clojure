(ns tasks.task5.src5-2)

(def num-threads 6)
(def block-size 10)

(defn pfilter [pred coll]
  (let [partition (fn part [cur]
                    (lazy-seq
                      (if-let [s (seq cur)]
                        (cons (take block-size cur) (part (drop block-size cur))))))
        rets (map (fn [x] (future (doall (filter pred x)))) (partition coll))
        step (fn step [[x & xs :as vs] fs]
               (lazy-seq
                 (if-let [s (seq fs)]
                   (concat (deref x) (step xs (rest s)))
                   (mapcat deref vs))))]
    (step rets (drop num-threads rets))))


(def parallel-primes
  (letfn
    [(lazy-sieve [sieve]
       (lazy-seq
         (cons
           (first sieve)
           (lazy-sieve (pfilter
                         (fn [x] (do (Thread/sleep 5) (not= 0 (mod x (first sieve)))))
                         sieve)))))]
    (lazy-sieve (iterate inc 2))))

(def primes
  (letfn
    [(lazy-sieve [sieve]
       (lazy-seq
         (cons
           (first sieve)
           (lazy-sieve (filter
                         (fn [x] (do (Thread/sleep 5) (not= 0 (mod x (first sieve)))))
                         sieve)))))]
    (lazy-sieve (iterate inc 2))))

(defn heavy-even? [num]
  (Thread/sleep 1)
  (even? num))

(defn -main [& args]
  (time (doall (pfilter heavy-even? (take 1003 (iterate inc 0)))))
  (time (doall (filter heavy-even? (take 1003 (iterate inc 0)))))

  (println "Performance comparison on task 2-2")

  (time (doall (take 50 parallel-primes)))
  (time (doall (take 50 primes))))