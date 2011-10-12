(ns utility
  (:use [clojure.contrib.string :only (replace-str)])
  (:import [org.joda.time DateTimeUtils]
           [java.io File IOException]))

(defn parse-int [s] (Integer/parseInt s))

(def re-captures (comp rest re-matches))

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

(defn resource [file]
  (str (File. (File. (System/getProperty "user.dir") "resources") file)))

(def fact-resource resource)

(defn lowercase-keyword [s]
  (keyword (.toLowerCase (replace-str " " "-" s))))

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

;(defn seq-of-all-seqs? [candidate]
;  (cond
;    (not (sequential? candidate))
;    (throw (RuntimeException. "expected seq of either all seqs or all non-seqs"))
;
;    (and (seq candidate) (every? sequential? candidate))
;    true
;
;    (every? (comp not sequential?) candidate)
;    false
;
;    :else
;    (throw (RuntimeException. "expected seq of either all seqs or all non-seqs"))))

(defn seq-of-all-seqs? [candidate]
  (and (sequential? candidate)
       (sequential? (first candidate))))

(defn config-file-name [] "config.cljdata")

(defn config []
  (try
    (-> (config-file-name) resource slurp (with-in-str (read)))
    (catch IOException e nil)))

(defn valid-config? []
  (let [cfg (config)]
    (and (= 2  (count cfg))
         (contains? cfg :gmail-address)
         (contains? cfg :password))))

(defn do-at* [date-time f]
  (DateTimeUtils/setCurrentMillisFixed (.getMillis date-time))
  (try
    (f)
    (finally (DateTimeUtils/setCurrentMillisSystem))))

(defmacro do-at [date-time & body]
  "like clojure.core.do except evalautes the expression at the given time"
  `(do-at* ~date-time
    (fn [] ~@body)))
