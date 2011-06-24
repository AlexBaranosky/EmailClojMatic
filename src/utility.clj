(ns utility
  (:require fs)
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
   (let [working-directory (but-last (fs/cwd) 1)]
      (str working-directory "resources\\" f)))
  
(defn fact-resource [f]
  ; (let [;working-directory (but-last (fs/cwd) 1)
         ;one-folder-up (join "\\" (drop-last (re-split #"\\" working-directory))
		 ;]
      (str "C:\\dev\\EmailClojMatic_Dev\\EmailClojMatic\\resources\\" f))
	  
(defn blank-or-nil? [s]
   (or (= s "") (nil? s)))  
   
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