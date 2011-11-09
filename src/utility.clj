(ns utility
  (:use [clojure.java.io :only (reader)])
  (:require [clojure.string :as str])
  (:import [java.io File IOException BufferedReader]))

(def re-captures (comp rest re-matches))

(defn re-match-seq [re s]
  (map first (re-seq re s)))

(defn resource [file]
  (str (File. (File. (System/getProperty "user.dir") "resources") file)))

(def fact-resource resource)

(defn lowercase-keyword [s]
  (keyword (.toLowerCase (str/replace s " " "-"))))

(def third (comp first rest rest))
(def fourth (comp first rest rest rest))
(def fifth (comp first rest rest rest rest))

(defn config-file-name [] "config.cljdata")

(defn config []
  (try
    (-> (config-file-name) resource slurp read-string)
    (catch IOException e nil)))

(defn valid-config? []
  (let [cfg (config)]
    (and (= 2  (count cfg))
         (contains? cfg :gmail-address)
         (contains? cfg :password))))

(defn read-lines
  "Like clojure.core/line-seq but opens f with reader.  Automatically
  closes the reader AFTER YOU CONSUME THE ENTIRE SEQUENCE."
  [f]
  (let [read-line (fn this [^BufferedReader rdr]
                    (lazy-seq
                     (if-let [line (.readLine rdr)]
                       (cons line (this rdr))
                       (.close rdr))))]
    (read-line (reader f))))
