(ns tasks.task4.src4-1
  (:require clojure.set,
            clojure.string))

;expression (the internal representation is DNF)
;there is only disjunction, conjuction and negation (of variables) expressions
(defn expression? [x]
  {:doc "Returns true if x is an expression, else false"}
  (and (list? x) (= 2 (count x)) (isa? (first x) ::expr)))

(defn- generate-expr [type args] {:doc "Generating *type* expression from args"}
  (list type args))

(defn- get-type [expr] {:doc "Return type of expression expr"}
  (first expr))

(defn args [expr] {:doc "Return arguments of expression expr"
                   :pre [(expression? expr)]}
  (second expr))

(defn print-expr [expr] {:doc "Print expression"
                         :pre [(expression? expr)]}
  (let [path "tasks.task4.src4-1/"]
    (println (clojure.string/replace (str expr) path ""))))

;constant
(derive ::const ::expr)

(defn constant [bool] {:doc "Generating a constant expression from boolean bool"
                       :pre [(boolean? bool)]}
  (generate-expr ::const bool))

(defn constant? [expr] {:doc "Returns true if expr is a constant expression, else false"
                        :pre [(expression? expr)]}
  (= ::const (get-type expr)))

;variable
(derive ::var ::expr)

(defn variable [name] {:doc "Generating a variable expression from a keyword name"
                       :pre [(keyword? name)]}
  (generate-expr ::var name))

(defn variable? [expr] {:doc "Returns true if expr is a variable expression, else false"
                        :pre [(expression? expr)]}
  (= ::var (get-type expr)))

;disjunction
(derive ::disj ::expr)

(declare negation?)

(defn- negation-variable? [expr] {:doc "Returns true if expr is a negation of variable"}
  (negation? expr))

(declare disjunction?)

(defn- collapse-disj [input-exprs] {:doc "Normalizes a list of expressions exprs for disjunction"}
  (let [exprs (reduce (fn [acc cur-expr]
                        (concat acc (if (disjunction? cur-expr) (args cur-expr) (list cur-expr))))
                      (list) input-exprs)
        other-exprs (distinct (remove constant? exprs))
        variable-exprs (filter variable? exprs)
        neg-variable-exprs (filter negation-variable? exprs)
        intersec (clojure.set/intersection
                   (set (map args variable-exprs))
                   (set (map args (map args neg-variable-exprs))))
        consts (if (< 0 (count intersec)) (list (constant true)) (filter constant? exprs))
        combined-const (not-every? false? (map args consts))]
    (cond
      (= true combined-const) (list (constant true))
      (= false combined-const) (if (empty? other-exprs)
                                 (list (constant false))
                                 other-exprs))
    ))

(defn disjunction [expr & rest]
  {:doc "Generating a disjunction expression from an expression expr and a list of expressions &rest"
   :pre [(every? expression? (cons expr rest))]}
  (let [normalized-exprs (collapse-disj (cons expr rest))]
    (if (= 1 (count normalized-exprs))
      (first normalized-exprs)
      (generate-expr ::disj normalized-exprs))))


(defn disjunction? [expr] {:doc "Returns true if expr is a disjunction expression, else false"
                           :pre [(expression? expr)]}
  (= ::disj (get-type expr)))

;conjunction
(derive ::conj ::expr)

(declare conjunction?)

(defn- collapse-conj [input-exprs] {:doc "Normalizes a list of expressions exprs for conjunction"}
  (let [exprs (reduce (fn [acc cur-expr]
                        (concat acc (if (conjunction? cur-expr) (args cur-expr) (list cur-expr))))
                      (list) input-exprs)
        other-exprs (distinct (remove constant? exprs))
        variable-exprs (filter variable? exprs)
        neg-variable-exprs (filter negation-variable? exprs)
        intersec (clojure.set/intersection
                   (set (map args variable-exprs))
                   (set (map args (map args neg-variable-exprs))))
        consts (if (< 0 (count intersec)) (list (constant false)) (filter constant? exprs))
        combined-const (every? true? (map args consts))]
    (cond
      (= false combined-const) (list (constant false))
      (= true combined-const) (if (empty? other-exprs)
                                (list (constant true))
                                other-exprs))
    ))

(defn conjunction [expr & rest]
  {:doc (str "Generating a conjunction expression from an expression expr and a list of expressions &rest."
             "Moreover, internal expressions cannot be disjunctions.")
   :pre [(every? expression? (cons expr rest))]}
  (let [normalized-exprs (collapse-conj (cons expr rest))]
    (if (= 1 (count normalized-exprs))
      (first normalized-exprs)
      (let [disj-expr (some #(if (disjunction? %) % false) normalized-exprs)]
        (if (nil? disj-expr)
          (generate-expr ::conj normalized-exprs)
          (let [rest-exprs (remove #(= disj-expr %) normalized-exprs)]
            (apply disjunction
                   (map
                     (fn [x] (apply conjunction (cons x rest-exprs)))
                     (args disj-expr)))))))))


(defn conjunction? [expr] {:doc "Returns true if expr is a conjunction expression, else false"
                           :pre [(expression? expr)]}
  (= ::conj (get-type expr)))

;negation
(derive ::neg ::expr)

(declare negation)

(def negation-rules
  (list
    [(fn [expr] (constant? expr))
     (fn [expr] (if (= true (args expr)) (constant false) (constant true)))]
    [(fn [expr] (variable? expr))
     (fn [expr] (generate-expr ::neg expr))]
    [(fn [expr] (disjunction? expr))
     (fn [expr] (apply conjunction (map negation (args expr))))]
    [(fn [expr] (conjunction? expr))
     (fn [expr] (apply disjunction (map negation (args expr))))]
    [(fn [expr] (negation? expr))
     (fn [expr] (args expr))]
    [(fn [_] :default)
     (fn [_] (throw (Exception.
                      (str "Expression is not in basis (negation, disjunction, conjunction"
                           "and not a constant or variable"))))]))

(defn negation [expr]
  {:doc "Generating a negation expression from a expression with the reduction of negation to variables"
   :pre [(expression? expr)]}
  ((some (fn [rule]
           (if ((first rule) expr)
             (second rule)
             false))
         negation-rules)
   expr))

(defn negation? [expr] {:doc "Returns true if expr is a negation of variable, else false"
                        :pre [(expression? expr)]}
  (= ::neg (get-type expr)))

;implication
(defn implication [pr cn]
  {:doc "Generating a implication in the form (:disj (:neg pr) cn) from expressions pr and cn"
   :pre [(and (expression? pr) (expression? cn))]}
  (disjunction (negation pr) cn))

;substitution constant
(declare substitute)

(def substitution-rules
  (list
    [(fn [expr _ _] (constant? expr))
     (fn [expr _ _] expr)]
    [(fn [expr var _] (and (variable? expr) (= expr var)))
     (fn [_ _ val] val)]
    [(fn [expr var _] (and (variable? expr) (not= expr var)))
     (fn [expr _ _] expr)]
    [(fn [expr _ _] (disjunction? expr))
     (fn [expr var val] (apply disjunction (map #(substitute % var val) (args expr))))]
    [(fn [expr _ _] (conjunction? expr))
     (fn [expr var val] (apply conjunction (map #(substitute % var val) (args expr))))]
    [(fn [expr _ _] (negation? expr))
     (fn [expr var val] (negation (substitute (args expr) var val)))]))

(defn substitute [expr var val]
  {:doc (str "Returns the expression obtained by substituting the constant expression val"
             "of the variable var into the expression expr")
   :pre [(and (expression? expr) (variable? var) (constant? val))]}
  ((some (fn [rule]
           (if ((first rule) expr var val)
             (second rule)
             false))
         substitution-rules)
   expr var val))

;print-v2
(defn- tab-print [lvl content]
  {:doc "Print with tabulation"
   :pre [(and (<= 0 lvl) (string? content))]}
  (println (str (apply str (repeat lvl "\t")) content)))

(defn- print-expr-imp-v2 [expr lvl]
  {:doc "Implementation of print expression version 2"
   :pre [(and (expression? expr) (<= 0 lvl))]}
  (cond
    (or (negation? expr) (disjunction? expr) (conjunction? expr))
    (do
      (tab-print lvl (str ":" (name (get-type expr))))
      (doall (map
               (fn [x] (apply print-expr-imp-v2 x))
               (map (fn [x] [x (inc lvl)]) (if (negation? expr) (list (args expr)) (args expr))))))
    (or (variable? expr) (constant? expr))
    (tab-print lvl (str ":" (name (get-type expr)) " " (args expr)))
    ))

(defn print-expr-v2 [expr] {:doc "Print expression version 2" :pre [(expression? expr)]}
  (print-expr-imp-v2 expr 0))

(defn -main [& args]
  (println "Expression 1:")
  (print-expr-v2 (conjunction (variable :x) (conjunction (variable :y) (variable :z))))
  (println "Expression 2:")
  (print-expr-v2 (conjunction (disjunction (negation (variable :a)) (variable :b))
                              (disjunction (variable :c) (variable :d))))
  (let [expr (negation (disjunction (implication (variable :x) (variable :y))
                                    (negation (implication (variable :y) (variable :z)))))]
    (println "Expression 3:")
    (print-expr-v2 expr)
    (println "Expression 3 with x = true:")
    (print-expr-v2 (substitute expr (variable :x) (constant true)))
    (println "Expression 3 with y = true:")
    (print-expr-v2 (substitute expr (variable :y) (constant true)))
    (println "Expression 3 with z = true:")
    (print-expr-v2 (substitute expr (variable :z) (constant true))))
  )
