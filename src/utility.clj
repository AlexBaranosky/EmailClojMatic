(ns utility
  (:use [clojure.java.io :only (reader)])
  (:import [java.io File IOException BufferedReader]))

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

(defn valid-email? [s]
  (re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" s))

(defn valid-gmail? [s]
  (and (valid-email? s) (.endsWith s "@gmail.com")))

;; TODO: move this stuff to a config namespace

(defn config-file-name [] "config.cljdata")

(defn config []
  (try
    (-> (config-file-name) resource slurp read-string)
    (catch IOException e nil)))

(defn valid-config? []
  (let [cfg (config)]
    (and (= (-> cfg keys set) #{:gmail-address :password})
         (valid-gmail? (:gmail-address cfg)))))