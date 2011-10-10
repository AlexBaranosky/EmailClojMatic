(ns email
  (:use [reminder :only (to-string)]
         [utility :only (config)]
         [clojure.contrib.string :only (join)])
  (:import [org.apache.commons.mail SimpleEmail]))

(defrecord EmailRecipient [name email-address])

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

(defn- disperse-emails [prefixing-message recipients ex]
  (doseq [r recipients]
    (send-email (:email-address r)
      (format "Could not send you your usual reminders. %s: \n%s" prefixing-message (.getMessage ex)))))

(def disperse-parse-error-emails   (partial disperse-emails "There was a problem parsing your reminders.txt"))
(def disperse-unknown-error-emails (partial disperse-emails "There was an unknown error"))

(defn disperse-history-file-missing-emails [recipients]
  (doseq [r recipients]
    (send-email (:email-address r)
      (format "Could not send you your usual reminders. Could not locate your reminder history file. It should be in the resources directory." ))))