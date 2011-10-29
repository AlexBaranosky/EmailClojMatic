(ns date-time-streams
  (:import [org.joda.time DateMidnight]))

(defn today+all-future-dates []
   (iterate #(.plusDays % 1) (DateMidnight.)))

(defn- next-day-of-week-in-future [day-num]
  (first (filter  #(= day-num (.. % dayOfWeek get)) (today+all-future-dates))))

(defn day-of-week-stream [day-num]
  (iterate #(.plusDays % 7) (next-day-of-week-in-future day-num)))

(defn- next-day-of-month-in-future [day-of-month]
  (first (filter #(= day-of-month (.. % dayOfMonth get)) (today+all-future-dates))))

(defn day-of-month-stream [day-of-month-num]
  (iterate #(.plusMonths % 1) (next-day-of-month-in-future day-of-month-num)))

(defn every-x-days-stream [start-date x-days]
  (iterate #(.plusDays % x-days) start-date))

(defn- next-date-in-future [month day-of-month]
  (first (filter #(and (= month (.. % monthOfYear get)) (= day-of-month (.. % dayOfMonth get))) (today+all-future-dates))))

(defn month+day-stream [month day-of-month]
  (iterate #(.plusYears % 1) (next-date-in-future month day-of-month)))

(defonce day-nums { :mondays 1 :tuesdays 2 :wednesdays 3 :thursdays 4 :fridays 5 :saturdays 6 :sundays 7 })
