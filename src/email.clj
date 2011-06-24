(ns email
  (:use reminder)
  (:use reminder-parsing)
  (:use [clojure.contrib.str-utils :only (str-join)])
  (:import [org.apache.commons.mail SimpleEmail]))

(defn format-reminder-email [reminders {:keys [name email-address]}]
  (let [from "\"EmailOMatic Reminder Service\""
        header (format "From: %s\nTo: %s <%s>\nSubject: " from name email-address)
        msg (str "The following reminders are coming up for tomorrow and the day after tomorrow: \n\n")
		formatted-reminders (->> reminders (map to-string) (map-indexed #(format "%s. %s" (inc %1) %2)) (str-join "\n\n"))]
    (str header msg formatted-reminders)))

(defn send-email [to-email-address message]  
   (doto (SimpleEmail.)
      (.addTo to-email-address)
      (.setMsg message)
      (.setHostName "smtp.gmail.com")
      (.setSslSmtpPort "465")
      (.setSSL true)
      (.setFrom "alexander.baranosky@gmail.com" "EmailOMatic Reminder Service")
      (.setSubject "EmailOMatic Reminder Service")
      (.setAuthentication "alexander.baranosky@gmail.com" "fdhghfdgh")
      (.send)))
	  
(defn send-reminder-email [reminders recipient]
  (send-email (:email-address recipient) 
              (format-reminder-email reminders recipient)))