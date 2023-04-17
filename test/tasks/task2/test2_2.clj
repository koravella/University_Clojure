(ns tasks.task2.test2-2
  (:use [tasks.task2.src2-2 :as src])
  (:require [clojure.test :refer :all]))

(deftest task3-1-test
  (testing "Testing primes"
    (is (= (take 10 src/primes) (list 2 3 5 7 11 13 17 19 23 29)))
    (is (thrown? Exception (nth src/primes -1)))
    ))

(run-tests 'tasks.task2.test2-2)

