(ns config
  (:use [utility :only (resource)])
  (:import [java.io IOException]))

(defn config-file-name [] "config.cljdata")

(defn config []
  (try
    (-> (config-file-name) resource slurp read-string)
    (catch IOException e nil)))

(letfn [(valid-email? [s]
          (re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" s))
        (valid-gmail? [s]
          (and (valid-email? s) (.endsWith s "@gmail.com")))]

  (defn valid-config? []
    (let [cfg (config)]
      (and (= (-> cfg keys set) #{:gmail-address :password })
        (valid-gmail? (:gmail-address cfg))))))