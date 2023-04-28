# University_Clojure

Tasks from the university functional programming course (spring 2023).

## Description of tasks

1. [Basic operations on data structures]

   A set of characters and the number n are specified.
   Describe a function that returns a list of all strings of length n
   consisting of these characters and not containing two identical
   characters running in a row.

   1. Solve the problem using elementary operations on sequences and
      recursion.
   
   2. Rewrite the program 1.1. so that all recursive calls are tail
      calls.
   
   3. Define the functions my-map and my-filter, similar to map (for
      one list) and filter, expressing them through reduce and basic
      operations on lists (cons, first, concat, etc.).
   
   4. Solve the problem using elementary operations on sequences and
      map/reduce/filter functionals.

2. [Sieve of Eratosthenes]

   1. Write a function that searches for the nth prime number using
      the sieve of Eratosthenes.
   
   2. Implement an infinite sequence of prime numbers

3. [Numerical integration]
   Implement a function (operator) that takes as an argument a function
   from one variable f and returns a function of one variable x that
   computes (numerically) the integral f(x) from 0 to x.
   You can use the trapezoid method with a constant step.

   When optimizing, proceed from the fact that the resulting prototype
   will be used for plotting (i.e. called repeatedly at different
   points).

   1. Optimize the function with memoization.
   
   2. Optimize the function with an infinite sequence of partial
      solutions.

4. [DNF]

   1. By analogy with the differentiation task, implement the
      representation of symbolic Boolean expressions with conjunction,
      disjunction, negation, implication operations.
      Expressions can include both Boolean constants and variables.
   
      Implement substitution of the variable value into an expression
      with its conversion to DNF.
      To provide extensibility for new operations (exclusive or,
      Pierce arrow, etc.), the code must be covered with tests, the
      API documented.

5. [Parallel processing of sequences]

   1. Implement a parallel version of filter (not necessarily lazy)
      using future. The number of simultaneously executed future
      objects is set by a constant (based, for example, on the number
      of cores), each object is responsible for calculating its own
      block of source data. The division into blocks should be
      carried out "manually", without using ready-made functions like
      partition (take and drop should be used to separate the
      sequence). Demonstrate an increase in performance compared to a
      default filter.
   
   2. Implement a lazy parallel filter, which should also work with
      infinite threads. Apply for task 2.2., show the performance gain.

6. [The problem of dining philosophers]

   1. Implement the problem of dining philosophers using STM in Clojure.
      Each fork is represented by a link (ref) containing a successful
      usage counter. Each philosopher is represented by a thread of
      execution, periods of reflection and absorption of spaghetti –
      delays. The number of philosophers, the number of "approaches"
      to the plate, the length of periods of reflection and eating
      of spaghetti set constants. 
      
      Conduct experiments with an even and odd number of philosophers.
      Count the number of unnecessary transaction restarts (use atom).
      How much does the solution differ from the optimal one in time?