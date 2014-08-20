(ns qr.persistence.riak-test
  (:require [clojure.test :refer :all]
            [qr.persistence.riak :as persistence]))

(defn get-uuid
  "get uuid from string"
  [input]
  (java.util.UUID/fromString input))

(deftest create-record-test
  (let [result (persistence/create-record "http://www.google.com.au")]
    (is (instance? java.util.UUID (get-uuid result)))))

(deftest read-record-test
  (let [result (persistence/read-record
      (persistence/create-record "http://www.google.com.au"))]
    (is (= (persistence/get-value result)
      {:destination "http://www.google.com.au"}))))
