(ns reminder
  (:use [date-time :only (first-not-in-past for-display in-past?)]))

; each seq in seq (or seq of seqs) of dates must be sorted in ascending order, or bad things happen!

(defn to-string [reminder]
  (if-let [date (first-not-in-past (:dates reminder))]
    (format "%s %s\n%s" (.. date dayOfWeek getAsText) (for-display date) (:message reminder))
    (format "%s\n%s"    "this reminder is not scheduled"                 (:message reminder))))

(defn due? [reminder]
  (if-let [next (first-not-in-past (:dates reminder))]
    (let [start-reminding-on (.minusDays next (:days-in-advance reminder))]
      (in-past? start-reminding-on))))