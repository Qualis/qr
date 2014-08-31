(ns qr.persistence.riak-test
  (:require [clojure.test :refer :all]
            [qr.persistence.riak :as persistence]
            [clojurewerkz.welle.core :as wc]
            [clojurewerkz.welle.buckets :as wb]))

(def list-of-id [])

(deftest create-record-test
  (let [result (persistence/create-record "http://www.google.com.au")]
    (persistence/delete-record result)))

(deftest exists-test
  (let [result (persistence/create-record "http://www.google.com.au")]
    (is (persistence/exists result))
    (persistence/delete-record result)))

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

; (deftest bulk-create-record-test
;   (doseq [i (range 100)]
;     (let [id (persistence/create-record (str "http://www.test" i ".com.au"))]
;       (def list-of-id (conj list-of-id id))))
;   (doseq [id (distinct list-of-id)]
;     (persistence/delete-record id))
;   (is (= (count (distinct list-of-id)) (count list-of-id))))
