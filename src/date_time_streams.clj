(ns date-time-streams
  (:use [utilize.seq :only (find-first)])
  (:import [org.joda.time DateMidnight]))

(defn today+all-future-dates []
   (iterate #(.plusDays % 1) (DateMidnight.)))

(defn day-of-week-stream [day-num]
  (letfn [(next-day-of-week-in-future [day-num]
           (find-first #(= day-num (.getDayOfWeek %)) (today+all-future-dates)))]
    (iterate #(.plusDays % 7) (next-day-of-week-in-future day-num))))

(defn day-of-month-stream [day-of-month-num]
  (letfn [(next-day-of-month-in-future [day-of-month]
           (find-first #(= day-of-month (.getDayOfMonth %)) (today+all-future-dates)))]
    (iterate #(.plusMonths % 1) (next-day-of-month-in-future day-of-month-num))))

(defn every-x-days-stream [start-date x-days]
  (iterate #(.plusDays % x-days) start-date))

(defn month+day-stream [month day-of-month]
  (letfn [(next-date-in-future [month day-of-month]
            (find-first #(and (= month (.getMonthOfYear %))
                              (= day-of-month (.getDayOfMonth %)))
                        (today+all-future-dates)))]
    (iterate #(.plusYears % 1) (next-date-in-future month day-of-month))))

(defonce day-nums { :mondays 1
                    :tuesdays 2
                    :wednesdays 3
                    :thursdays 4
                    :fridays 5
                    :saturdays 6
                    :sundays 7 })