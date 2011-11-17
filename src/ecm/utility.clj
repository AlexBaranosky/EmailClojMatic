(ns ecm.utility
  (:use [clojure.java.io :only (reader)])
  (:import [java.io File BufferedReader]))

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

(defn resource [file]
  (str (File. (File. (System/getProperty "user.dir") "resources") file)))

(def fact-resource resource)
