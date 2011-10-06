(ns joda-time-cop
  (:import (org.joda.time DateTimeUtils)))

;; TODO: make this elegantly handle thrown exceptions from f
(defn do-at* [date-time f]
  (DateTimeUtils/setCurrentMillisFixed (.getMillis date-time))
  (let [result (f)]
	  (DateTimeUtils/setCurrentMillisSystem)
	  result))

(defmacro do-at [date-time & body]
  "like clojure.core.do except evalautes the expression at the given time"
  `(do-at* ~date-time
    (fn [] ~@body)))
