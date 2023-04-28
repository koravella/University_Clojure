(ns tasks.task6.src6-1)

(def kThinkers 5)
(def kSteps 10)
(def kTimeReflection 0)
(def kTimeEating 40)

(def forks (doall (map (fn [_] [(ref 0) (atom 0)]) (range 0 kThinkers))))

;(Clockwise numeration) for Philosopher A, forks A and (A-1) (mod kThinker) are intended
(defn- ForkDistribution [id]
  [(nth forks id) (nth forks (mod (- id 1) kThinkers))])

(defn- Eating [left-fork right-fork]
  (reduce
    (fn [_ _]
      (dosync
        (swap! (second left-fork) inc)
        (swap! (second right-fork) inc)
        (alter (first left-fork) inc)
        (alter (first right-fork) inc)
        (Thread/sleep kTimeEating))
      (Thread/sleep kTimeReflection))
    0 (range 0 kSteps)))

(def thinkers
  (doall
    (map
      (fn [pair] (new Thread (fn [] (Eating (first pair) (second pair)))))
      (map ForkDistribution (range 0 kThinkers)))))

(defn Lunch []
  (doall (map (fn [x] (.start x)) thinkers))
  (doall (map (fn [x] (.join x)) thinkers)))

(defn PrintlnForks []
  (println (map (fn [x] [@(first x) @(second x)]) forks)))

(defn Restarts []
  (->> forks
       (map (fn [x] (- @(second x) @(first x))))
       (apply +)
       (* 0.5)
       (int)))

(defn -main [& args]
  (time (Lunch))
  (printf "Lunch time of one philosopher: %d msecs\n" (* (+ kTimeReflection kTimeEating) kSteps))
  (print "Forks [ref, atom]: ")
  (PrintlnForks)
  (printf "Number of restarts: %d" (Restarts)))