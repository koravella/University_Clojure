(ns tasks.task4.test4-1
  (:use [tasks.task4.src4-1 :as src])
  (:require [clojure.test :refer :all]))


(defn part-equiv [expr1 expr2]
  (let [get-setargs (fn [x] (if (or (constant? x) (variable? x))
                              (set (list (args x)))
                              (set (args x))))
        args1 (get-setargs expr1), args2 (get-setargs expr2)]
    (if (and (disjunction? expr1) (disjunction? expr2))
      (= (set (map get-setargs args1)) (set (map get-setargs args2)))
      (= args1 args2))))

(deftest test-representations
  (let [expr (list ::src/expr nil)
        non-expr (list nil nil)]

    (testing "testing expression"
      (is (expression? expr))
      (is (not (expression? non-expr)))
      (is (= (second expr) (args expr)))
      (is (thrown? AssertionError (args non-expr)))
      (is (thrown? AssertionError (print-expr non-expr))))

    (testing "Testing constants"
      (is (constant? (constant true)))
      (is (not (constant? expr)))
      (is (= false (args (constant false))))
      (is (thrown? AssertionError (constant 1)))
      (is (thrown? AssertionError (constant? non-expr))))

    (testing "Testing variables"
      (is (variable? (variable :x)))
      (is (not (variable? expr)))
      (is (= :x (args (variable :x))))
      (is (thrown? AssertionError (variable 1)))
      (is (thrown? AssertionError (variable? non-expr))))

    (testing "Testing disjunctions"
      (is (part-equiv (disjunction (constant true) (constant false) (variable :x)) (constant true)))
      (is (part-equiv (disjunction (variable :x) (negation (variable :x))) (constant true)))
      (is (part-equiv (disjunction (variable :x) (variable :x) (constant false)) (variable :x)))
      (is (part-equiv
            (disjunction (variable :x) (negation (variable :z)))
            (disjunction (negation (variable :z)) (variable :x))))
      (is (= (args (disjunction (variable :x) (variable :z))) (list (variable :x) (variable :z))))
      (is (part-equiv
            (disjunction (variable :x) (variable :y) (variable :z))
            (disjunction (variable :x) (disjunction (variable :y) (variable :z)))))
      (is (thrown? AssertionError (disjunction non-expr)))
      (is (thrown? AssertionError (disjunction? non-expr))))

    (testing "Testing conjunctions"
      (is (part-equiv (conjunction (constant true) (constant false) (variable :x)) (constant false)))
      (is (part-equiv (conjunction (variable :x) (negation (variable :x))) (constant false)))
      (is (part-equiv (conjunction (variable :x) (variable :x) (constant true)) (variable :x)))
      (is (part-equiv
            (conjunction (variable :x) (negation (variable :z)))
            (conjunction (negation (variable :z)) (variable :x))))
      (is (= (args (conjunction (variable :x) (variable :z))) (list (variable :x) (variable :z))))
      (is (part-equiv
            (conjunction (variable :x) (variable :y) (variable :z))
            (conjunction (variable :x) (conjunction (variable :y) (variable :z)))))
      (is (part-equiv
            (conjunction (variable :a) (disjunction (variable :c) (variable :d)))
            (disjunction (conjunction (variable :a) (variable :c)) (conjunction (variable :a) (variable :d)))))
      (is (part-equiv
            (conjunction (disjunction (variable :a) (variable :b)) (disjunction (variable :c) (variable :d)))
            (disjunction (conjunction (variable :a) (variable :c)) (conjunction (variable :a) (variable :d))
                         (conjunction (variable :b) (variable :c)) (conjunction (variable :b) (variable :d)))))
      (is (thrown? AssertionError (conjunction non-expr)))
      (is (thrown? AssertionError (conjunction? non-expr))))

    (testing "Testing negations"
      (is (negation? (negation (variable :x))))
      (is (not (negation? (variable :x))))
      (is (part-equiv (variable :x) (args (negation (variable :x)))))
      (is (part-equiv
            (negation (conjunction (variable :x) (variable :y)))
            (conjunction (negation (variable :x)) (negation (variable :y)))))
      (is (part-equiv
            (negation (disjunction (variable :x) (variable :y)))
            (disjunction (negation (variable :x)) (negation (variable :y)))))
      (is (part-equiv (constant false) (negation (constant true))))
      (is (part-equiv (variable :x) (negation (negation (variable :x)))))
      (is (thrown? Exception (negation expr)))
      (is (thrown? AssertionError (negation non-expr)))
      (is (thrown? AssertionError (negation? non-expr))))

    (testing "Testing implication"
      (is (part-equiv (implication (variable :x) (variable :y)) (disjunction (negation (variable :x)) (variable :y))))
      (is (part-equiv (implication (constant true) (constant false)) (constant false)))
      (is (thrown? AssertionError (implication expr non-expr)))
      (is (thrown? AssertionError (implication non-expr expr))))
    ))

(deftest substitution
  (let [var-x (variable :x)
        var-y (variable :y)
        var-z (variable :z)
        val-t (constant true)
        val-f (constant false)]
    (testing "testing substitute"
      (is (part-equiv val-t (substitute val-t var-x val-f)))
      (is (part-equiv val-f (substitute val-f var-x val-f)))
      (is (part-equiv val-t (substitute var-x var-x val-t)))
      (is (part-equiv val-f (substitute var-x var-x val-f)))
      (is (part-equiv var-y (substitute var-y var-x val-t)))
      (is (part-equiv var-y (substitute var-y var-x val-f)))
      (is (part-equiv val-t (substitute (disjunction var-x var-y) var-x val-t)))
      (is (part-equiv var-y (substitute (disjunction var-x var-y) var-x val-f)))
      (is (part-equiv (disjunction var-x var-y) (substitute (disjunction var-x var-y) var-z val-t)))
      (is (part-equiv (disjunction var-x var-y) (substitute (disjunction var-x var-y) var-z val-f)))
      (is (part-equiv var-y (substitute (conjunction var-x var-y) var-x val-t)))
      (is (part-equiv val-f (substitute (conjunction var-x var-y) var-x val-f)))
      (is (part-equiv (conjunction var-x var-y) (substitute (conjunction var-x var-y) var-z val-t)))
      (is (part-equiv (conjunction var-x var-y) (substitute (conjunction var-x var-y) var-z val-f)))
      (is (part-equiv val-f (substitute (negation var-x) var-x val-t)))
      (is (part-equiv val-t (substitute (negation var-x) var-x val-f)))
      (is (part-equiv (negation var-y) (substitute (negation var-y) var-x val-f))))))



(run-tests 'tasks.task4.test4-1)