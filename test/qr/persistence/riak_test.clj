(ns qr.persistence.riak-test
  (:require [clojure.test :refer :all]
            [qr.persistence.riak :as persistence]
            [clj-time.local :as time]
            [clj-time.format :as time-format]))

(def ^:const LARGE_NUMBER_TEST_COUNT 1000)
(def ^:const TEST_DATA "coconuts")

(def date-formatter (time-format/formatters :mysql))
(def list-of-id [])

(deftest creates-record
  (let [id (persistence/create-record TEST_DATA)]
))

(deftest uses-existing-id
  (let [id (persistence/create-record TEST_DATA)]
    (let [second-id (persistence/create-record TEST_DATA)]
      (persistence/delete-record id)
      (persistence/delete-record second-id)
      (is (= id second-id)))))

(deftest finds-existing-by-id
  (let [id (persistence/create-record TEST_DATA)]
    (is (persistence/exists? id))
    (persistence/delete-record id)))

(deftest reads-record-by-id
  (let [id (persistence/create-record TEST_DATA)]
    (let [record (persistence/read-record id)]
      (persistence/delete-record id)
      (is (= (persistence/get-value record)
        {:destination TEST_DATA})))))

(deftest deletes-record-by-id
  (let [id (persistence/create-record TEST_DATA)]
    (persistence/delete-record id)
    (let [record (persistence/read-record id)]
      (is (nil? (persistence/get-value record))))))

; (deftest creates-and-deletes-large-number-of-records
;   (println (str "1: "(time-format/unparse date-formatter (time/local-now))))
;   (doseq [i (range LARGE_NUMBER_TEST_COUNT)]
;     (let [id (persistence/create-record (str "coconuts: " i))]
;       (def list-of-id (conj list-of-id id))))
;   (println (str "2: "(time-format/unparse date-formatter (time/local-now))))
;   (doseq [id (distinct list-of-id)]
;     (persistence/delete-record id))
;   (println (str "3: "(time-format/unparse date-formatter (time/local-now))))
;   (is (= (count (distinct list-of-id)) (count list-of-id))))
