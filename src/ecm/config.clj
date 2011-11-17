(ns ecm.config
  (:use [ecm.utility :only (resource)])
  (:import [java.io IOException]))

(defn config-file-name [] "config.cljdata")

(defn config []
  (try
    (-> (config-file-name) resource slurp read-string)
    (catch IOException e nil)))

(letfn [(valid-gmail? [s]
          (re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@gmail.com" s))]

  (defn valid-config? []
    (let [cfg (config)]
      (and (= (-> cfg keys set) #{:gmail-address :password })
        (valid-gmail? (:gmail-address cfg))))))