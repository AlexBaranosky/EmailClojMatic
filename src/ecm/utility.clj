(ns ecm.utility
  (:use [clojure.java.io :only (reader)]
        [fs.core :only [file]])
  (:import [java.io BufferedReader]))

(defn read-lines
  "Like clojure.core/line-seq but opens f with reader.  Automatically
  closes the reader AFTER YOU CONSUME THE ENTIRE SEQUENCE."
  [f]
  (letfn [(read-line [^BufferedReader rdr]
            (lazy-seq
              (if-let [line (.readLine rdr)]
                (cons line (read-line rdr))
                (.close rdr))))]
    (read-line (reader f))))
