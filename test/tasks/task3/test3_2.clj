(ns tasks.task3.test3-2
  (:use [tasks.task3.src3-2 :as src])
  (:require [clojure.test :refer :all]))

(defn close-to [x y eps]
  (<= (abs (- x y)) eps))

(deftest task3-2-test
  (let [f1 #(* % %), f2 (fn [x] x), tol 1e-3]
    (testing "Testing antider"
      (is (close-to ((src/antider f1 0.1) 0) 0.0 tol))
      (is (close-to ((src/antider f1 0.02) 3) 9.0 tol))
      (is (close-to ((src/antider f2 0.01) 2) 2 tol))
      (is (thrown? Exception ((src/antider f2 0.01) -1))))
    ))

(run-tests 'tasks.task3.test3-2)
