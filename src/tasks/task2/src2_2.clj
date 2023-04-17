(ns tasks.task2.src2-2)

(def primes
  (letfn
    [(lazy-sieve [sieve]
       (lazy-seq
         (cons
           (first sieve)
           (lazy-sieve (remove
                         (fn [x] (= 0 (mod x (first sieve))))
                         sieve)))))]
    (lazy-sieve (iterate inc 2))))

(defn -main [& args]
  (println (take 10 primes))
  (println (nth primes 1000)))
