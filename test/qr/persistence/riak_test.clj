(ns qr.persistence.riak-test
  (:require [clojure.test :refer :all]
            [qr.persistence.riak :as persistence]))

(defn get-uuid
  "get uuid from string"
  [input]
  (java.util.UUID/fromString input))

(deftest create-record-test
  (let [result (persistence/create-record "http://www.google.com.au")]
    (persistence/delete-record result)
    (is (instance? java.util.UUID (get-uuid result)))))

(deftest read-record-test
  (let [id (persistence/create-record "http://www.google.com.au")]
    (let [result (persistence/read-record id)]
      (persistence/delete-record id)
      (is (= (persistence/get-value result)
        {:destination "http://www.google.com.au"})))))

(deftest delete-record-test
  (let [id (persistence/create-record "http://www.google.com.au")]
    (persistence/delete-record id)
    (let [result (persistence/read-record id)]
      (is (nil? (persistence/get-value result))))))
