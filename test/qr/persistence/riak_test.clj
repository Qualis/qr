(ns qr.persistence.riak-test
  (:require [clojure.test :refer :all]
            [qr.persistence.riak :as persistence]))

(defn get-uuid
  "get uuid from string"
  [input]
  (java.util.UUID/fromString input))

(deftest save-test
  (let [result (persistence/save "http://www.google.com.au")]
    (is (instance? java.util.UUID (get-uuid result)))))
