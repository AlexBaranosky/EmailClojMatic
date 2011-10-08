(ns email
  (:use [reminder :only (to-string)])
  (:use [utility :only (config)])
  (:use [clojure.contrib.string :only (join)])
  (:import [org.apache.commons.mail SimpleEmail]))

(defn format-reminder-email [reminders {:keys [name email-address]}]
  (let [from "\"EmailOMatic Reminder Service\""
        header (format "From: %s\nTo: %s <%s>\nSubject: " from name email-address)
        msg (str "The following reminders are coming up: \n\n")
		formatted-reminders (->> reminders (map to-string) (map-indexed #(format "%s. %s" (inc %1) %2)) (join "\n\n"))]
    (str header msg formatted-reminders)))

(defn send-email* [to-email-address message from-address authentication-password]
  (doto (SimpleEmail.)
    (.addTo to-email-address)
    (.setMsg message)
    (.setHostName "smtp.gmail.com")
    (.setSslSmtpPort "465")
    (.setSSL true)
    (.setFrom from-address "EmailOMatic Reminder Service")
    (.setSubject "EmailOMatic Reminder Service")
    (.setAuthentication from-address authentication-password)
    (.send)))

(defn send-email [to-email-address message]
  (let [cfg (config)]
    (send-email* to-email-address message (:gmail-address cfg) (:password cfg))))

(defn send-reminder-email [reminders recipient]
  (send-email (:email-address recipient)
              (format-reminder-email reminders recipient)))

(defn- disperse-email [prefixing-message recipients ex]
  (doseq [r recipients]
    (send-email (:email-address r)
      (format "Could not send you your usual reminders. %s: \n%s" prefixing-message (.getMessage ex)))))

(def disperse-parse-error-email   (partial disperse-email "There was a problem parsing your reminders.txt"))
(def disperse-unknown-error-email (partial disperse-email "There was an unknown error"))