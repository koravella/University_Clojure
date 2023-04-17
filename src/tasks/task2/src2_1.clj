(ns tasks.task2.src2-1)

(defn sieve-cycle [sieve cnt]
  (remove
    (fn [x] (= 0 (mod x (first sieve))))
    sieve))

(defn sieveERT [n]
  (first
    (reduce
      sieve-cycle
      (range 2 (+ (* n n) 2))
      (range (- n 1)))))

(println (for [i (range 1 11)] (sieveERT i)))
(println (sieveERT 1000))


(defn sieveERT-v2 [n]
  (let [size (+ (* n n) 2)]
    (first
      (reduce
        (fn [sieve cnt]
          (let [cur (first sieve)]
            (if (> (* cur cur) size)
              (rest sieve)
              (remove
                (fn [x] (= 0 (mod x cur)))
                sieve))))
        (range 2 size)
        (range 0 (- n 1))))))

(println (sieveERT-v2 10000))