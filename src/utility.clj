(ns utility
  (:require [clojure.contrib.string :as str]))

(defn parse-int [s] (Integer/parseInt s))

(defonce re-match? (comp not not re-matches))

(defonce re-captures (comp rest re-matches))

(defn re-match-seq [re s]
  (map first (re-seq re s)))

(defn trim [s] (.trim s))

(defn only [coll]
  (if (= 1 (count coll))
    (first coll)
    (throw (RuntimeException. (format "should have precisely one item, but had: %s" (count coll))))))
  
(defn but-last [s n]
   (if (> n (.length s))
       ""
      (.substring s 0 (- (.length s) n))))  
  
(defn resource [f]
  (str "C:\\dev\\EmailClojMatic_Dev\\resources\\" f))
  
(defn fact-resource [f]
  ; (let [;working-directory (but-last (fs/cwd) 1)
         ;one-folder-up (join "\\" (drop-last (re-split #"\\" working-directory))
		 ;]
      (str "C:\\dev\\EmailClojMatic_Dev\\resources\\" f))
   
(defn lowercase-keyword [s]
  (keyword (.toLowerCase (str/replace-str " " "-" s))))   
  
(def third (comp first rest rest))  
(def fourth (comp first rest rest rest))  
(def fifth (comp first rest rest rest rest))  
  
(defn ordinal-to-int [ord]
  (let [digits (but-last ord 2)]
    (Integer/parseInt digits)))
  
(defn ordinalize [int]
  (let [num+ (partial str int)]
    (if (contains? #{11 12 13} (mod int 100))
      (num+ "th")
	  (case (mod int 10)
  	    1 (num+ "st")
	    2 (num+ "nd")
	    3 (num+ "rd")
	    (num+ "th")))))  

;; from clojure.core for 1.3
(defn some-fn
  "Takes a set of predicates and returns a function f that returns the first logical true value
  returned by one of its composing predicates against any of its arguments, else it returns
  logical false. Note that f is short-circuiting in that it will stop execution on the first
  argument that triggers a logical true result against the original predicates."
  {:added "1.3"}
  ([p]
     (fn sp1
       ([] nil)
       ([x] (p x))
       ([x y] (or (p x) (p y)))
       ([x y z] (or (p x) (p y) (p z)))
       ([x y z & args] (or (sp1 x y z)
                           (some p args)))))
  ([p1 p2]
     (fn sp2
       ([] nil)
       ([x] (or (p1 x) (p2 x)))
       ([x y] (or (p1 x) (p1 y) (p2 x) (p2 y)))
       ([x y z] (or (p1 x) (p1 y) (p1 z) (p2 x) (p2 y) (p2 z)))
       ([x y z & args] (or (sp2 x y z)
                           (some #(or (p1 %) (p2 %)) args)))))
  ([p1 p2 p3]
     (fn sp3
       ([] nil)
       ([x] (or (p1 x) (p2 x) (p3 x)))
       ([x y] (or (p1 x) (p2 x) (p3 x) (p1 y) (p2 y) (p3 y)))
       ([x y z] (or (p1 x) (p2 x) (p3 x) (p1 y) (p2 y) (p3 y) (p1 z) (p2 z) (p3 z)))
       ([x y z & args] (or (sp3 x y z)
                           (some #(or (p1 %) (p2 %) (p3 %)) args)))))
  ([p1 p2 p3 & ps]
     (let [ps (list* p1 p2 p3 ps)]
       (fn spn
         ([] nil)
         ([x] (some #(% x) ps))
         ([x y] (some #(or (% x) (% y)) ps))
         ([x y z] (some #(or (% x) (% y) (% z)) ps))
         ([x y z & args] (or (spn x y z)
                             (some #(some % args) ps)))))))
	
(def sequable? (some-fn seq? map? vector? set?))
	  
(defn seq-of-seqs? [candidate]
  (and (sequable? candidate) 
       (sequable? (first candidate))))
	  
;; stole from core.clj
(defmacro ^{:private true} assert-args [fnname & pairs]
  `(do (when-not ~(first pairs)
         (throw (IllegalArgumentException. ~(str fnname " requires " (second pairs)))))
     ~(let [more (nnext pairs)]
        (when more
          (list* 'assert-args fnname more)))))  
  
(defmacro seq-let
  "If test is a seq, evaluates then with binding-form bound to the value of 
  test, if not, yields else"
  ([bindings then]
   `(seq-let ~bindings ~then nil))
  ([bindings then else & oldform]
   (assert-args seq-let
     (and (vector? bindings) (nil? oldform)) "a vector for its binding"
     (= 2 (count bindings)) "exactly 2 forms in binding vector")
   (let [[form test] bindings]
     `(let [temp# ~test]
        (if (seq temp#)
          (let [~form temp#]
            ~then)
          ~else)))))