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
  (str "/home/alex/workspace/EmailClojMaticInDevlopment/resources/" f))

(defn fact-resource [f]
  ; (let [;working-directory (but-last (fs/cwd) 1)
         ;one-folder-up (join "\\" (drop-last (re-split #"\\" working-directory))
		 ;]
      (resource f))

(defn lowercase-keyword [s]
  (keyword (.toLowerCase (str/replace-str " " "-" s))))

(def third (comp first rest rest))
(def fourth (comp first rest rest rest))
(def fifth (comp first rest rest rest rest))

(defn ordinal-to-int [ord]
  (let [digits (but-last ord 2)]
    (Integer/parseInt digits)))

(defn ordinalize [int]
  (if (contains? #{11 12 13} (mod int 100))
    (str int "th")
	(case (mod int 10)
  	  1 (str int "st")
	  2 (str int "nd")
	  3 (str int "rd")
	  (str int "th"))))

(defn seq-of-seqs? [candidate]
  (and (sequential? candidate)
       (sequential? (first candidate))))
