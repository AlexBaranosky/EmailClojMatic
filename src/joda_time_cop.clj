(ns joda-time-cop
  (:import (org.joda.time DateTimeUtils)))

(defn freeze [date-time f]
  (DateTimeUtils/setCurrentMillisFixed (.getMillis date-time))
  (let [result (f)]
	(DateTimeUtils/setCurrentMillisSystem)
	result) )

(defmacro do-at [date-time & body]
  `(freeze ~date-time
    (fn [] ~@body)))
