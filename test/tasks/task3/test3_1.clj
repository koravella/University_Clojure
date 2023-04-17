(ns tasks.task3.test3-1
  (:use [tasks.task3.src3-1 :as src])
  (:require [clojure.test :refer :all]))

(defn close-to [x y eps]
  (<= (abs (- x y)) eps))

(deftest task3-1-test
  (let [f1 #(* % %), f2 (fn [x] x), tol 1e-3]
    (testing "Testing antider0"
      (is (close-to ((src/antider0 f1 0.1) 0) 0.0 tol))
      (is (close-to ((src/antider0 f1 0.02) 3) 9.0 tol))
      (is (close-to ((src/antider0 f2 0.01) 2) 2 tol))
      (is (thrown? Exception ((src/antider0 f2 0.01) -1)))
      )
    (testing "Testing antider1"
      (is (close-to ((src/antider1 f1 0.1) 0) 0.0 tol))
      (is (close-to ((src/antider1 f1 0.02) 3) 9.0 tol))
      (is (close-to ((src/antider1 f2 0.01) 2) 2 tol))
      (is (thrown? Exception ((src/antider1 f2 0.01) -1))))
    (testing "testing graph"
      (is (= (list 0 0.5 1.0 1.5) (graph f2 0.5 2)))
      (is (= (list) (graph f2 1 0))))))

(run-tests 'tasks.task3.test3-1)