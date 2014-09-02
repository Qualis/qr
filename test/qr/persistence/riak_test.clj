(ns qr.persistence.riak-test
  (:require [clojure.test :refer :all]
            [qr.persistence.riak :as persistence]
            [clj-time.local :as time]
            [clj-time.format :as time-format]))

(def date-formatter (time-format/formatters :mysql))
(def list-of-id [])

(deftest create-record-test
  (let [result (persistence/create-record "http://www.google.com.au")]
    (persistence/delete-record result)))

(deftest exists-test
  (let [result (persistence/create-record "http://www.google.com.au")]
    (is (persistence/exists? result))
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
;   (println (str "1: "(time-format/unparse date-formatter (time/local-now))))
;   (doseq [i (range 100000)]
;     (let [id (persistence/create-record (str "http://www.test" i ".com.au"))]
;       (def list-of-id (conj list-of-id id))))
;   (println (str "2: "(time-format/unparse date-formatter (time/local-now))))
;   (doseq [id (distinct list-of-id)]
;     (persistence/delete-record id))
;   (println (str "3: "(time-format/unparse date-formatter (time/local-now))))
;   (is (= (count (distinct list-of-id)) (count list-of-id))))
