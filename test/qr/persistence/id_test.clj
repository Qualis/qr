(ns qr.persistence.id-test
  (:require [clojure.test :refer :all]
            [qr.persistence.id :as id-generator]))

(def verified-exists false)

(defn verify-exists
  [id]
  (let [has-checked verified-exists]
    (def verified-exists true)
    (if (false? has-checked) true false)))

(deftest checks-for-existing
  (let [result (id-generator/generate-id verify-exists)]
    (is (true? verified-exists))))
