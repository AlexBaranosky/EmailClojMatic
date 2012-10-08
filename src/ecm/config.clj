(ns ecm.config
  (:use [clojure.java.io :only (resource)]
        [clojure.core.incubator :only [-?>]])
  (:import [java.io IOException]))

(defn config-file-name [] "config.cljdata")

(defn config []
  (try
    (-?> (config-file-name) resource slurp read-string)
    (catch IOException e nil)))

(letfn [(valid-gmail? [s]
          (re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@gmail.com" s))]

  (defn valid-config? []
    (let [cfg (config)]
      (and (= (keys cfg) [:gmail-address :password])
           (valid-gmail? (:gmail-address cfg))))))